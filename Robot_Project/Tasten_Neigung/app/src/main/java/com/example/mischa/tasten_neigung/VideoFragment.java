package com.example.mischa.tasten_neigung;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.ResourceBundle;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mischa.tasten_neigung.LoginRobotActivity.message;

/**
 * Class for viewing video with usage of an webview.
 * @author Uwe Simon
 */
public class VideoFragment extends Fragment {

    private ExitVideoInterface listener;
    private ImageButton exitButton;

    private WebView view;
    private ImageButton foto;
    boolean readAccepted;
    boolean writeAccepted;

    /**
     * Initializes the class with an empty constructor.
     */
    public VideoFragment() {}

    /**
     * Class attaches the interface ExitVideoInterface.
     * @param context Context of this Fragment.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ExitVideoInterface) {
            listener = (ExitVideoInterface) context;
        } else {
            throw new ClassCastException(context.toString() + "Parent must implement exit");
        }
    }

    /**
     * Initalizes variables and configures them.
     * @param inflater Infaltes layout activity_video.
     * @param container Necessary to inflate layout activity_video.
     * @param savedInstanceState Not used, but necessary, because method is overwritten.
     * @return View with instance activity_video
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View videoView =  inflater.inflate(R.layout.activity_video, container, false);

        exitButton = (ImageButton) videoView.findViewById(R.id.exitVideo);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.exitVideoFragment();
            }
        });

        view = (WebView) videoView.findViewById(R.id.web);
        foto = (ImageButton) videoView.findViewById(R.id.takePicture);

        setWebView();
        setFotoButton();

        return videoView;
    }

    /**
     * Sets content of webview as an dynamic image in an html with 95% width and height of webview.
     */
    private void setWebView() {
        view.setBackgroundColor(Color.TRANSPARENT);
        String htmlPart1 = getString(R.string.html1); //"<html><body><img src=\"http://";
        String htmlPart3 = getString(R.string.html3);
        String htmlPart5 = getString(R.string.html5);
        String htmlPart7 = getString(R.string.html7);
        String mime = getString(R.string.mime);
        String encoding = getString(R.string.encoding);
        String port = getString(R.string.port);

        view.setWebViewClient(new WebViewClient());

        // no scrolling in view
        String width = getString(R.string.width);
        String height = getString(R.string.height);

        String html = htmlPart1+message+port+htmlPart3+width+htmlPart5+height+htmlPart7;
        view.loadDataWithBaseURL(null,html,mime,encoding,null);
    }

    /**
     * Sets an onClickListener to foto and starts a thread, if button is pressed.
     */
    private void setFotoButton() {
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    takePicture.run();
                }catch(Exception e) {
                    ; //Ignore multiple touches, otherwise it was crashing.
                }
            }
        });
    }

    /**
     * Checks necessity of asking for permission on Runtime.
     * @return Boolean of necessity of asking for permission.
     */
    private boolean checkPermissionNecessity(){

        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    /**
     * Get answer of permission to read and write storage
     * @param permsRequestCode Code to ask for permission
     * @param permissions Array of permissions to ask
     * @param grantResults Result of the questions for permission
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        if (permsRequestCode ==  200){
            readAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;

            writeAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED;

            if (!readAccepted || !writeAccepted) {
                Toast.makeText(getContext(),R.string.video_errorPermission, Toast.LENGTH_LONG).show();

            }
        }
    }

    /**
     * Thread asks for permission of reading and writing and takes picture, if permissions are
     * granted.
     */
    private Runnable takePicture = new Runnable() {

        @Override
        public void run() {
            readAccepted = true;
            writeAccepted = true;

            Picture picture = view.capturePicture();
            Bitmap b = Bitmap.createBitmap(picture.getWidth(),
                    picture.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);

            picture.draw(c);
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1; // starts at 0
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);

            boolean necessity = checkPermissionNecessity();

            if (necessity) {
                // ask for permission
                String[] perms = {getString(R.string.permissionRead), getString(R.string.permissionWrite)};

                int permsRequestCode = 200;

                requestPermissions(perms, permsRequestCode);
            }
            if (readAccepted && writeAccepted) {
                FileOutputStream fos = null;
                try {
                    String file = R.string.fileNameStart + year + getString(R.string.fileNameMiddle)
                            + month + getString(R.string.fileNameMiddle) + day
                            + getString(R.string.fileNameMiddle) + hour
                            + getString(R.string.fileNameMiddle) + minute
                            + getString(R.string.fileNameMiddle) + second
                            + getString(R.string.fileNameEnd);

                    fos = new FileOutputStream(getString(R.string.filePath) + file);
                    if (fos != null) {
                        b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                        Toast.makeText(getContext(), file + getString(R.string.saved), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    //Real message ("Permisson denied").
                    //Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                }
            }

        }
    };
}
