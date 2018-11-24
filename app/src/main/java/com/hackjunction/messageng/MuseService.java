package com.hackjunction.messageng;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.choosemuse.libmuse.AnnotationData;
import com.choosemuse.libmuse.ConnectionState;
import com.choosemuse.libmuse.Eeg;
import com.choosemuse.libmuse.LibmuseVersion;
import com.choosemuse.libmuse.MessageType;
import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseArtifactPacket;
import com.choosemuse.libmuse.MuseConfiguration;
import com.choosemuse.libmuse.MuseConnectionListener;
import com.choosemuse.libmuse.MuseConnectionPacket;
import com.choosemuse.libmuse.MuseDataListener;
import com.choosemuse.libmuse.MuseDataPacket;
import com.choosemuse.libmuse.MuseDataPacketType;
import com.choosemuse.libmuse.MuseFileFactory;
import com.choosemuse.libmuse.MuseFileReader;
import com.choosemuse.libmuse.MuseFileWriter;
import com.choosemuse.libmuse.MuseListener;
import com.choosemuse.libmuse.MuseManagerAndroid;
import com.choosemuse.libmuse.MuseVersion;
import com.choosemuse.libmuse.Result;
import com.choosemuse.libmuse.ResultLevel;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class MuseService extends Service {
	private static final String TAG = "MuseService";

	public MuseService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private MuseManagerAndroid manager;

    private ConnectionListener connectionListener;
    private DataListener dataListener;
    private Muse muse;

    private final Map<MuseDataPacketType, Double> latest = new ConcurrentHashMap<>();

    private LocalBroadcastManager localBroadcastManager;

    /**
     * We will be updating the UI using a handler instead of in packet handlers because
     * packets come in at a very high frequency and it only makes sense to update the UI
     * at about 60fps. The update functions do some string allocation, so this reduces our memory
     * footprint and makes GC pauses less frequent/noticeable.
     */
    private final Handler handler = new Handler();

    /**
     * To save data to a file, you should use a MuseFileWriter.  The MuseFileWriter knows how to
     * serialize the data packets received from the headband into a compact binary format.
     * To read the file back, you would use a MuseFileReader.
     */
    private final AtomicReference<MuseFileWriter> fileWriter = new AtomicReference<>();

    private final AtomicReference<Handler> fileHandler = new AtomicReference<>();

    private class ConnectionListener extends MuseConnectionListener {
        @Override
        public void receiveMuseConnectionPacket(MuseConnectionPacket museConnectionPacket, Muse muse) {
            MuseService.this.receiveMuseConnectionPacket(museConnectionPacket, muse);
        }
    }

    private class DataListener extends MuseDataListener {
        @Override
        public void receiveMuseDataPacket(MuseDataPacket museDataPacket, Muse muse) {
            MuseService.this.receiveMuseDataPacket(museDataPacket, muse);
        }

        @Override
        public void receiveMuseArtifactPacket(MuseArtifactPacket museArtifactPacket, Muse muse) {
            MuseService.this.receiveMuseArtifactPacket(museArtifactPacket, muse);
        }
    }

    private class MuseL extends MuseListener {
        @Override
        public void museListChanged() {
            MuseService.this.museListChanged();
        }
    }

    /**
     * You will receive a callback to this method each time there is a change to the
     * connection state of one of the headbands.
     * @param p     A packet containing the current and prior connection states
     * @param muse  The headband whose state changed.
     */
    public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {

        final ConnectionState current = p.getCurrentConnectionState();

        // Format a message to show the change of connection state in the UI.
        final String status = p.getPreviousConnectionState() + " -> " + current;
        Log.i(TAG, status);

        // Update the UI with the change in connection state.
        handler.post(new Runnable() {
            @Override
            public void run() {

                //final TextView statusText = (TextView) findViewById(R.id.con_status);
                //statusText.setText(status);
                Log.d(TAG, status);

                final MuseVersion museVersion = muse.getMuseVersion();
                //final TextView museVersionText = (TextView) findViewById(R.id.version);
                // If we haven't yet connected to the headband, the version information
                // will be null.  You have to connect to the headband before either the
                // MuseVersion or MuseConfiguration information is known.
                if (museVersion != null) {
                    final String version = museVersion.getFirmwareType() + " - "
                            + museVersion.getFirmwareVersion() + " - "
                            + museVersion.getProtocolVersion();
                    //museVersionText.setText(version);
                    Log.d(TAG, version);
                } else {
                    //museVersionText.setText(R.string.undefined);
                    Log.d(TAG, "Version: undefined");
                }
            }
        });

        if (current == ConnectionState.DISCONNECTED) {
            Log.i(TAG, "Muse disconnected:" + muse.getName());
            // Save the data file once streaming has stopped.
            saveFile();
            // We have disconnected from the headband, so set our cached copy to null.
            this.muse = null;
        }
    }

    private double aggregateChannels(MuseDataPacket packet) {
        double count = 0;
        double sum = 0;
        for (Eeg eeg : Eeg.values()) {
            double it = packet.getEegChannelValue(eeg);
            if (Double.isNaN(it)) {
                continue;
            }
            sum += it;
            count += 1;
        }
        return sum / count;
    }

    /**
     * You will receive a callback to this method each time the headband sends a MuseDataPacket
     * that you have registered.  You can use different listeners for different packet types or
     * a single listener for all packet types as we have done here.
     * @param p     The data packet containing the data from the headband (eg. EEG data)
     * @param muse  The headband that sent the information.
     */
    public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
        //Log.d(TAG, "Received data packet of type " + p.packetType().toString());
        writeDataPacketToFile(p);

        switch (p.packetType()) {
            case ALPHA_RELATIVE:
            case BETA_RELATIVE:
            case GAMMA_RELATIVE:
            case DELTA_RELATIVE:
            case THETA_RELATIVE:
                latest.put(p.packetType(), aggregateChannels(p));
                //Log.d(TAG, latest.toString());
                break;
            default:
                break;
        }
    }

    /**
     * You will receive a callback to this method each time an artifact packet is generated if you
     * have registered for the ARTIFACTS data type.  MuseArtifactPackets are generated when
     * eye blinks are detected, the jaw is clenched and when the headband is put on or removed.
     * @param p     The artifact packet with the data from the headband.
     * @param muse  The headband that sent the information.
     */
    public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
        Log.i(TAG, p.toString());
    }

    /**
     * You will receive a callback to this method each time a headband is discovered.
     * In this example, we update the spinner with the MAC address of the headband.
     */
    public void museListChanged() {
        Log.i(TAG, "Muse list changed.");
        final List<Muse> list = manager.getMuses();

        if (list.isEmpty()) {
            Log.w(TAG, "Muse list is empty!");
            return;
        }

        final Muse m = list.get(0);
        if (this.muse != null) {
            this.muse.disconnect();
            this.muse = null;
        }
        this.muse = m;

        connect(m);
    }

    public void refresh() {
        // Start listening for nearby or paired Muse headbands. We call stopListening
        // first to make sure startListening will clear the list of headbands and start fresh.
        manager.stopListening();
        manager.startListening();
    }

    public void connect(Muse muse) {
        Log.d(TAG, "Connecting to " + muse.getName());
        // Listening is an expensive operation, so now that we know
        // which headband the user wants to connect to we can stop
        // listening for other headbands.
        manager.stopListening();

        // Check that we actually have something to connect to.
        // Cache the Muse that the user has selected.
        // Unregister all prior listeners and register our data listener to
        // receive the MuseDataPacketTypes we are interested in.  If you do
        // not register a listener for a particular data type, you will not
        // receive data packets of that type.
        muse.unregisterAllListeners();
        muse.registerConnectionListener(connectionListener);
        muse.registerDataListener(dataListener, MuseDataPacketType.EEG);
        muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_RELATIVE);
        muse.registerDataListener(dataListener, MuseDataPacketType.ACCELEROMETER);
        muse.registerDataListener(dataListener, MuseDataPacketType.BATTERY);
        muse.registerDataListener(dataListener, MuseDataPacketType.DRL_REF);
        muse.registerDataListener(dataListener, MuseDataPacketType.QUANTIZATION);

        // Initiate a connection to the headband and stream the data asynchronously.
        muse.runAsynchronously();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (this.muse != null) {
            Log.i(TAG, "Disconnecting from muse");
            this.muse.disconnect();
        }
    }

    @Override
    public void onCreate() {
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this);

        this.manager = MuseManagerAndroid.getInstance();
        this.manager.setContext(this);

        Log.i(TAG, "LibMuse version=" + LibmuseVersion.instance().getString());

        connectionListener = new ConnectionListener();
        dataListener = new DataListener();
        manager.setMuseListener(new MuseL());

        manager.startListening();

        // Start up a thread for asynchronous file operations.
        // This is only needed if you want to do File I/O.
        fileThread.start();

        handler.post(tickUi);
    }

    /**
     * The runnable that is used to update the UI at 60Hz.
     *
     * We update the UI from this Runnable instead of in packet handlers
     * because packets come in at high frequency -- 220Hz or more for raw EEG
     * -- and it only makes sense to update the UI at about 60fps. The update
     * functions do some string allocation, so this reduces our memory
     * footprint and makes GC pauses less frequent/noticeable.
     */
    private final Runnable tickUi = new Runnable() {
        @Override
        public void run() {
            // TODO: Update UI.
            if (latest.isEmpty()) {
                return;
            }
            Log.d(TAG, latest.toString());
            Intent intent = new Intent(MessageNG.WAVE_UPDATE);
            intent.putExtra("alpha", latest.getOrDefault(MuseDataPacketType.ALPHA_RELATIVE, Double.NaN));
            localBroadcastManager.sendBroadcast(intent);
            handler.postDelayed(tickUi, 1000 / 40);
        }
    };

    //--------------------------------------
    // File I/O

    /**
     * We don't want to block the UI thread while we write to a file, so the file
     * writing is moved to a separate thread.
     */
    private final Thread fileThread = new Thread() {
        @Override
        public void run() {
            Looper.prepare();
            fileHandler.set(new Handler());
            final File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            final File file = new File(dir, "new_muse_file.muse" );
            // MuseFileWriter will append to an existing file.
            // In this case, we want to start fresh so the file
            // if it exists.
            if (file.exists()) {
                file.delete();
            }
            Log.i(TAG, "Writing data to: " + file.getAbsolutePath());
            fileWriter.set(MuseFileFactory.getMuseFileWriter(file));
            Looper.loop();
        }
    };

    /**
     * Writes the provided MuseDataPacket to the file.  MuseFileWriter knows
     * how to write all packet types generated from LibMuse.
     * @param p     The data packet to write.
     */
    private void writeDataPacketToFile(final MuseDataPacket p) {
        Handler h = fileHandler.get();
        if (h != null) {
            h.post(new Runnable() {
                @Override
                public void run() {
                    fileWriter.get().addDataPacket(0, p);
                }
            });
        }
    }

    /**
     * Flushes all the data to the file and closes the file writer.
     */
    private void saveFile() {
        Handler h = fileHandler.get();
        if (h != null) {
            h.post(new Runnable() {
                @Override public void run() {
                    MuseFileWriter w = fileWriter.get();
                    // Annotation strings can be added to the file to
                    // give context as to what is happening at that point in
                    // time.  An annotation can be an arbitrary string or
                    // may include additional AnnotationData.
                    w.addAnnotationString(0, "Disconnected");
                    w.flush();
                    w.close();
                }
            });
        }
    }

    /**
     * Reads the provided .muse file and prints the data to the logcat.
     * @param name  The name of the file to read.  The file in this example
     *              is assumed to be in the Environment.DIRECTORY_DOWNLOADS
     *              directory.
     */
    private void playMuseFile(String name) {
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(dir, name);

        final String tag = "Muse File Reader";

        if (!file.exists()) {
            Log.w(tag, "file doesn't exist");
            return;
        }

        MuseFileReader fileReader = MuseFileFactory.getMuseFileReader(file);

        // Loop through each message in the file.  gotoNextMessage will read the next message
        // and return the result of the read operation as a Result.
        Result res = fileReader.gotoNextMessage();
        while (res.getLevel() == ResultLevel.R_INFO && !res.getInfo().contains("EOF")) {

            MessageType type = fileReader.getMessageType();
            int id = fileReader.getMessageId();
            long timestamp = fileReader.getMessageTimestamp();

            Log.i(tag, "type: " + type.toString() +
                    " id: " + Integer.toString(id) +
                    " timestamp: " + String.valueOf(timestamp));

            switch(type) {
                // EEG messages contain raw EEG data or DRL/REF data.
                // EEG derived packets like ALPHA_RELATIVE and artifact packets
                // are stored as MUSE_ELEMENTS messages.
                case EEG:
                case BATTERY:
                case ACCELEROMETER:
                case QUANTIZATION:
                case GYRO:
                case MUSE_ELEMENTS:
                    MuseDataPacket packet = fileReader.getDataPacket();
                    Log.i(tag, "data packet: " + packet.packetType().toString());
                    break;
                case VERSION:
                    MuseVersion version = fileReader.getVersion();
                    Log.i(tag, "version" + version.getFirmwareType());
                    break;
                case CONFIGURATION:
                    MuseConfiguration config = fileReader.getConfiguration();
                    Log.i(tag, "config" + config.getBluetoothMac());
                    break;
                case ANNOTATION:
                    AnnotationData annotation = fileReader.getAnnotation();
                    Log.i(tag, "annotation" + annotation.getData());
                    break;
                default:
                    break;
            }

            // Read the next message.
            res = fileReader.gotoNextMessage();
        }
    }
}
