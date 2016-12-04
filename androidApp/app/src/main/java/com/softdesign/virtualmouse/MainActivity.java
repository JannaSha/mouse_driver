package com.softdesign.virtualmouse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.app.AlertDialog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {

    private static final int MOUSE_MOVE = 7;
    private static final int MOUSE_DOUBLE_CLICK = 6;
    private static final int MOUSE_LEFT_BUTTON_PRESS = 1;
    private static final int MOUSE_RIGHT_BUTTON_PRESS = 2;
    private static final int RECEIVE_INTERVAL_MILE_SECONDS = 1000;
    private static final int SCREEN_WIDTH_INDEX = 1;
    private static final int SCREEN_HEIGHT_INDEX = 2;
    private static final int DOUBLE_CLICK_TIME_DELTA = 300;

    private Button leftMouseButton, rightMouseButton;
    private TextView mainTextInfo, hostText, portText;
    private EditText portEdit, hostEdit;
    private boolean isConnected = false;

    int screenWidth = 0, screenHeight = 0;
    double scaleX = 0.0, scaleY = 0.0;
    int mouseX = 0, mouseY = 0;

    private static Socket serverSocket = null;
    private static long lastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTextInfo = (TextView) findViewById(R.id.ApplicationInfoText);
        leftMouseButton = (Button) findViewById(R.id.LeftMouseButton);
        rightMouseButton = (Button) findViewById(R.id.RightMouseButton);
        hostText = (TextView) findViewById(R.id.hostText);
        portText = (TextView) findViewById(R.id.portText);
        hostEdit = (EditText) findViewById(R.id.hostEdit);
        portEdit = (EditText) findViewById(R.id.portEdit);

        leftMouseButton.setOnClickListener(leftButtonClickListener);
        rightMouseButton.setOnClickListener(rightButtonClickListener);

        leftMouseButton.setVisibility(View.INVISIBLE);
        rightMouseButton.setVisibility(View.INVISIBLE);
        portText.setVisibility(View.INVISIBLE);
        portEdit.setVisibility(View.INVISIBLE);
        hostText.setVisibility(View.INVISIBLE);
        hostEdit.setVisibility(View.INVISIBLE);

        WindowManager windowManager = this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        mainTextInfo.setText("You have to connect");
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        scaleX = 900.0 / screenWidth;
        scaleY = 1440.0 / screenHeight;
    }

    View.OnClickListener leftButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SendMouseCommandThread senComThread =
                    new SendMouseCommandThread(MOUSE_LEFT_BUTTON_PRESS);
            new Thread(senComThread).start();
        }
    };

    View.OnClickListener rightButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SendMouseCommandThread senComThread =
                    new SendMouseCommandThread(MOUSE_RIGHT_BUTTON_PRESS);
            new Thread(senComThread).start();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        long clickTime = System.currentTimeMillis();
        SendMouseCommandThread senComThr;
        mouseX = (int)motionEvent.getX();
        mouseY = (int)motionEvent.getY();
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA)
                senComThr = new SendMouseCommandThread(MOUSE_DOUBLE_CLICK);
            else
                senComThr = new SendMouseCommandThread(MOUSE_MOVE);
//            showMessage("OK", "DOWN");
            new Thread(senComThr).start();
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
//                showMessage("OK", "Move");
                senComThr = new SendMouseCommandThread(MOUSE_MOVE);
                new Thread(senComThr).start();
        }
        lastClickTime = clickTime;
        return true;
    }

    // Сохранение ативности перед ее уничтожением
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mainTextInfo", mainTextInfo.getText().toString());

        outState.putBoolean("WasConnected", false);

        if(isConnected) {
            DisconnectThread discThr = new DisconnectThread();
            new Thread(discThr).start();
            outState.putBoolean("WasConnected", true);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savesInstanceState) {
        super.onRestoreInstanceState(savesInstanceState);
        mainTextInfo.setText(savesInstanceState.getString("mainTextInfo"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ExitAction) {
            CloseAppThread clsAppThr = new CloseAppThread();
            new Thread(clsAppThr).start();
            System.exit(0);
            return true;
        }
        else if (id == R.id.DisconnectAction) {
            leftMouseButton.setVisibility(View.INVISIBLE);
            rightMouseButton.setVisibility(View.INVISIBLE);
            portText.setVisibility(View.INVISIBLE);
            portEdit.setVisibility(View.INVISIBLE);
            hostText.setVisibility(View.INVISIBLE);
            hostEdit.setVisibility(View.INVISIBLE);
            DisconnectThread disconnectThread = new DisconnectThread();
            new Thread(disconnectThread).start();
            mainTextInfo.setText("You have to connect");
            return true;
        }
        else if (id == R.id.ConnectAction) {
            portText.setVisibility(View.INVISIBLE);
            portEdit.setVisibility(View.INVISIBLE);
            hostText.setVisibility(View.INVISIBLE);
            hostEdit.setVisibility(View.INVISIBLE);
            leftMouseButton.setVisibility(View.VISIBLE);
            rightMouseButton.setVisibility(View.VISIBLE);
            ConnectThread connectThread = new ConnectThread();
            new Thread(connectThread).start();
            mainTextInfo.setText("Connected");
            return true;
        }
        else if (id == R.id.HelpAction) {
            if (isConnected) {
                showMessage("ERROR", "You should disconnect first!");
            }
            else {
                leftMouseButton.setVisibility(View.INVISIBLE);
                rightMouseButton.setVisibility(View.INVISIBLE);
                portText.setVisibility(View.INVISIBLE);
                portEdit.setVisibility(View.INVISIBLE);
                hostText.setVisibility(View.INVISIBLE);
                hostEdit.setVisibility(View.INVISIBLE);
                mainTextInfo.setText(R.string.HelpInfo);
                mainTextInfo.setVisibility(View.VISIBLE);
            }
            return true;
        }
        else if (id == R.id.SelectHostPortAction) {
            if (isConnected) {
                showMessage("ERROR", "You should disconnect first!");
            }
            else {
                leftMouseButton.setVisibility(View.INVISIBLE);
                rightMouseButton.setVisibility(View.INVISIBLE);
                portText.setVisibility(View.VISIBLE);
                portEdit.setVisibility(View.VISIBLE);
                hostText.setVisibility(View.VISIBLE);
                hostEdit.setVisibility(View.VISIBLE);
                mainTextInfo.setText("Select host and port");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class ConnectThread implements Runnable {
        public void run() {
            if (isConnected) {
                showMessage("Information", "You should already been connected!");
            }
            else {
                String host = "";
                Integer port = 0;
                if (hostEdit.getText().toString().length() > 0
                        && portEdit.getText().toString().length() > 0) {
                    try {
                        host = hostEdit.getText().toString();
                        port = Integer.valueOf(portEdit.getText().toString());
                    } catch (TypeNotPresentException ex) {
                        showMessage("Error", "Incorrect port or host");
                    }
                    try {
                        serverSocket = new Socket();
                        serverSocket.connect(new InetSocketAddress(host, port), 10000);
                        showMessage("OK", "You have connected!");
                        serverSocket.getOutputStream().write("connected".getBytes());
                        isConnected = true;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showMessage("Error", "Connection error");
                    }
                }
                else
                    showMessage("Error", "Input port and host");
            }
        }

    }

    class SendMouseCommandThread implements Runnable {
        private int mouseCommandType;
        public SendMouseCommandThread(int mouseCommandType) {
            this.mouseCommandType = mouseCommandType;
        }

        public void run() {
            if (isConnected) {
                try {
                    String mouseCommand = Integer.toString(mouseCommandType) + " " +
                            Integer.toString((int) (mouseX * scaleX)) + " " +
                            Integer.toString((int) (mouseY * scaleX)) + "\n";

                    serverSocket.getOutputStream().write(mouseCommand.getBytes());
                } catch (Exception ex) {
                    showMessage("Send command exception", ex.getMessage());
                }
            }
        }
    }


    class DisconnectThread implements Runnable {
        public void run() {
            try {
                if (!isConnected)
                    showMessage("Disconnect error", "You have already been disconnected!");
                else {
                    serverSocket.close();
                    isConnected = false;
                    showMessage("Disconnect ok!", "You have been successfully disconnected!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showMessage("Disconnect error!", ex.getMessage());
            }
        }
    }

    class CloseAppThread implements Runnable {
        public void run() {
            try {
                if (isConnected)
                    serverSocket.close();
            } catch (Exception ex) {
                showMessage("close all thread exception", ex.getMessage());
            }
        }
    }

    public void showMessage(final String title, final String message) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
                dlgAlert.setMessage(message);
                dlgAlert.setTitle(title);
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        });
    }
}