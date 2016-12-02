package com.softdesign.virtualmouse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.app.AlertDialog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private static final int MOUSE_MOVE = 7;
    private static final int MOUSE_LEFT_BUTTON_PRESS = 1;
    private static final int MOUSE_RIGHT_BUTTON_PRESS = 2;
    private static final int BUFFER_SIZE = 64;
    private static final int BROADCAST_PORT_NUMBER = 9000;
    private static final String BROADCAST_IP_ADDRESS = "10.0.2.2";
    private static final int RECEIVE_INTERVAL_MILE_SECONDS = 1000;
    private static final int UDP_PORT_INDEX = 0;
    private static final int SCREEN_WIDTH_INDEX = 1;
    private static final int SCREEN_HEIGHT_INDEX = 2;

    private Button leftMouseButton, rightMouseButton;
    private TextView mainTextInfo;
    private Socket serverSocket = null;
    private boolean isConnected = false;

    int screenWidth = 0, screenHeight = 0;
    double scaleX = 0.0, scaleY = 0.0;
    int mouseX = 0, mouseY = 0;
    int mouseCommandType = 0;

    private int UdpPortNumber = 0;
    private int TcpPortNumber = 0;

    private DatagramSocket serverDatagramSocket = null;
    private InetAddress serverAddress = null;

    private static Socket soc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTextInfo = (TextView) findViewById(R.id.ApplicationInfoText);
        leftMouseButton = (Button)findViewById(R.id.LeftMouseButton);
        rightMouseButton = (Button)findViewById(R.id.RightMouseButton);

        leftMouseButton.setOnClickListener(leftButtonClickListener);
        rightMouseButton.setOnClickListener(rightButtonClickListener);

//        leftMouseButton.setVisibility(View.VISIBLE);
//        rightMouseButton.setVisibility(View.VISIBLE);

        WindowManager windowManager = this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    soc = new Socket(BROADCAST_IP_ADDRESS, BROADCAST_PORT_NUMBER);
                    ShowMessage("OK", "You have connected!");
                    soc.getOutputStream().write("connected".getBytes());
                    isConnected = true;
                } catch (IOException e) {
                    ShowMessage("Connect exception", e.getMessage());
                }
            }
        }).start();


        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        scaleX = 900.0 / screenWidth;
        scaleY = 1440.0 / screenHeight;

//        FindServerThread findserverThread = new FindServerThread();
//        new Thread(findserverThread).start();
    }

//    private void FindServer(){
//        try {
//            DatagramSocket findServerSocket = new DatagramSocket();
//            findServerSocket.setBroadcast(true);
//
//            byte[] serverMessage = new byte[BUFFER_SIZE];
//
//            // Упаковка сообщения в пакет
//            DatagramPacket serverAnswer = new DatagramPacket(serverMessage, serverMessage.length);
//
//            String message = "New client";
//            DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(),
//                    InetAddress.getByName(BROADCAST_IP_ADDRESS), BROADCAST_PORT_NUMBER);
//
//            findServerSocket.send(datagramPacket);
//            findServerSocket.setSoTimeout(RECEIVE_INTERVAL_MILE_SECONDS);
//            // findServerSocket.receive(serverAnswer);
//
//            serverAddress = serverAnswer.getAddress();
//
//            String portNumberStr = new String(serverAnswer.getData());
//            portNumberStr = portNumberStr.trim().replaceAll("\n", "");
//            TcpPortNumber = Integer.parseInt(portNumberStr);
//
//            findServerSocket.close();
//
//        } catch (SocketException e) {
//            ShowMessage("Network exception", e.getMessage());
//        } catch (SocketTimeoutException ex) {
//            ShowMessage("Find server exception", ex.getMessage());
//        } catch (UnknownHostException ex) {
//            ShowMessage("Unknown host", ex.getMessage());
//        } catch (IOException ex) {
//            ShowMessage("error!", ex.getMessage());
//        }
//    };

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
        mouseX = (int)motionEvent.getX();
        mouseY = (int)motionEvent.getY();
        ShowMessage("Send", mouseX + " " + mouseY);
        SendMouseCommandThread senComThr = new SendMouseCommandThread(MOUSE_MOVE);
        new Thread(senComThr).start();
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
            DisconnectThread disconnectThread = new DisconnectThread();
            new Thread(disconnectThread).start();
            return true;
        }
        else if (id == R.id.ConnectAction) {
            ConnectThread connectThread = new ConnectThread();
            new Thread(connectThread).start();
            return true;
        }
        else if (id == R.id.HelpAction) {
            if (isConnected) {
                System.out.println("ERROR, you should disconnect first!");
            }
            else {
                leftMouseButton.setVisibility(View.INVISIBLE);
                rightMouseButton.setVisibility(View.INVISIBLE);
                mainTextInfo.setText(R.string.HelpInfo);
                mainTextInfo.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class ConnectThread implements Runnable {
        public void run() {
            try {
                if (isConnected) {
                    System.out.println("Connect error, You should already benn connected!");
                }
                else {
//                    serverSocket = new Socket(serverAddress, TcpPortNumber);
//
//                    BufferedReader inputStream = new BufferedReader(
//                            new InputStreamReader(serverSocket.getInputStream()));
//
//                    char[] buffer = new char[BUFFER_SIZE];
//                    inputStream.read(buffer, 0, BUFFER_SIZE);
//
//                    String serverData = String.valueOf(buffer);
//
//                    String[] stringsArray = serverData.split(" ");
//                    UdpPortNumber = Integer.parseInt(stringsArray[UDP_PORT_INDEX]);
//                    scaleX = (double)Double.parseDouble(stringsArray[SCREEN_WIDTH_INDEX])
//                            / screenWidth;
//                    scaleY = (double)Double.parseDouble(stringsArray[SCREEN_HEIGHT_INDEX])
//                            / screenHeight;


//                    serverDatagramSocket = new DatagramSocket();
//                    serverDatagramSocket.setBroadcast(false);

//                    System.out.println("Connection ok!");
//                    isConnected = true;

                    soc = new Socket(BROADCAST_IP_ADDRESS, BROADCAST_PORT_NUMBER);
                    ShowMessage("OK", "You have connected!");
                    soc.getOutputStream().write("connected".getBytes());
                    isConnected = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Connection exception!");

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
                            Integer.toString((int) (mouseY * scaleX));
//                    DatagramPacket datagramPacket = new DatagramPacket(mouseCommand.getBytes(),
//                            mouseCommand.length(), serverAddress, UdpPortNumber);
//                    serverDatagramSocket.send(datagramPacket);
                    soc.getOutputStream().write(mouseCommand.getBytes());
                    ShowMessage("OK!", "MessageSend!");
                } catch (Exception ex) {
                    ShowMessage("Send command exception", ex.getMessage());
                }
            }
        }
    }

//    class FindServerThread implements Runnable {
//        public void run() {
//            FindServer();
//        }
//    }

    class DisconnectThread implements Runnable {
        public void run() {
            try {
                if (!isConnected) {
                    ShowMessage("Disconnect error", "You have already been disconnected!");
                }
                else {
                    soc.close();
//                    serverSocket.close();
//                    serverDatagramSocket.close();
                    isConnected = false;
                    ShowMessage("Disconnect ok!", "You have been successfully disconnected!");
                    ShowMainAppInfo();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                ShowMessage("Disconnect error!", ex.getMessage());
                ShowMainAppInfo();
            }
        }
    }

    class CloseAppThread implements Runnable {
        public void run() {
            try {
                if (isConnected) {
//                    serverSocket.close();
//                    serverDatagramSocket.close();
                    soc.close();
                }
            } catch (Exception ex) {
                ShowMessage("close all thread exception", ex.getMessage());
            }
        }
    }

    public void ShowOnlyControlComponents() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainTextInfo.setVisibility(View.INVISIBLE);
                leftMouseButton.setVisibility(View.VISIBLE);
                rightMouseButton.setVisibility(View.VISIBLE);
            }
        });
    }
    public void ShowMainAppInfo() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leftMouseButton.setVisibility(View.VISIBLE);
                rightMouseButton.setVisibility(View.VISIBLE);
                mainTextInfo.setText(R.string.app_name);
                mainTextInfo.setVisibility(View.VISIBLE);
            }
        });
    }

    public void ShowMessage(final String title, final String message) {
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
