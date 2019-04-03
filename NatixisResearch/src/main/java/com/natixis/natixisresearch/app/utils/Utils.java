package com.natixis.natixisresearch.app.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.NatixisPdfReader;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.generic.NatixisObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.artifex.mupdfdemo.MuPDFActivity;

/**
 * Created by Thibaud on 09/04/2017.
 */
public class Utils {


    /**
     * @return platform id 1 Android 2 Amazon 3 Blackberry
     */
    public final static int PLATFORM_BLACKBERRY = 2;
    public final static int PLATFORM_ANDROID = 1;

    public static int getPlatform() {
        if (System.getProperty("os.name").contains("qnx")) {
            return PLATFORM_BLACKBERRY;
        } else {
            return PLATFORM_ANDROID;
        }
    }

    private static Pattern dotNetPattern = Pattern.compile("^\\/Date\\(([0-9]*)([\\+-]+)([0-9]*)\\)\\/$");

    public static Date convertDotNetDate(String date) {
        //Example : /Date(1391075730000+0100)/
        String dateformat = "yyyyMMddHHmmss";
        String dateformatUtc = "yyyyMMddHHmmssZ";
        Calendar cal = Calendar.getInstance();
        //  cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateWithUtc = null;
        Matcher m = dotNetPattern.matcher(date);
        if (m.find()) {
            String strTimestamp = m.group(1);
            String strGmtSign = m.group(2);
            String strGmtVal = m.group(3);

            long timestamp = Long.parseLong(strTimestamp);
            cal.setTimeInMillis(timestamp);
          /*  String dateWithoutUTc = new SimpleDateFormat(dateformat).format(cal.getTime());
            if(strGmtSign.equals("+")){
                strGmtSign="-";
            }else if(strGmtSign.equals("-")){
                strGmtSign="+";
            }
            dateWithoutUTc+=strGmtSign+strGmtVal;

            try {
                dateWithUtc = new SimpleDateFormat(dateformatUtc).parse(dateWithoutUTc);
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

        }
        return cal.getTime();
    }

    public static void openDocument(Context context, ResearchDocument document) {
        boolean opened = false;
        Intent intent = null;

        NatixisObjectMapper mapper = new NatixisObjectMapper();
        String docJson = "";
        try {
            docJson = mapper.writeValueAsString(document);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (Utils.getPlatform() == Utils.PLATFORM_ANDROID) {

            Uri uri = Uri.parse(document.getFilepath());
            intent = new Intent(context, NatixisPdfReader.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra(NatixisPdfReader.JSON_DOCUMENT, docJson);

            intent.setData(uri);

            try {
                context.startActivity(intent);
                opened = true;

            } catch (ActivityNotFoundException e) {
                opened = false;
            }

        }

        if (!opened) { // If muPDF doesn't work or not android platform, try
            // using external reader
            File file = new File(document.getFilepath());
            if (file.exists()) {
                System.out.println("File exist :  " + file.getAbsolutePath()+ " / doc : "+document.getTitle()+ "("+ document.getDocumentID()+")");
                try {
                    Uri fileUri = Uri.fromFile(file);

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(fileUri);
                    i.setType("application/pdf");

                    context.startActivity(i);

                } catch (Exception e) {

                    Toast.makeText(context, "Impossible d'ouvrir le fichier..." + e.getMessage() + " " + e.getClass().getName(), Toast.LENGTH_LONG).show();

                }
            } else {
                Toast.makeText(context, "Fichier introuvable..." + file.getName(), Toast.LENGTH_LONG).show();

            }
        }
    }

    public static boolean isNotNullOrEmpty(String text) {
        return text != null && text.length() > 0;
    }


    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static long calculateMaxAge() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTimeInMillis() - System.currentTimeMillis();
    }


    /**
     * Return the size of a directory in bytes
     */
    public static long dirSize(File dir) {

        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if (fileList[i].isDirectory()) {
                    result += dirSize(fileList[i]);
                } else {
                    // Sum the file size in bytes
                    result += fileList[i].length();
                }
            }
            return result; // return the file size
        }
        return 0;
    }

    /**
     * Return the size of a directory in bytes
     */
    public static void clearDir(File dir) {

        if (dir.exists()) {
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if (fileList[i].isDirectory()) {
                    clearDir(fileList[i]);
                    fileList[i].delete();

                } else {
                    // Sum the file size in bytes
                    fileList[i].delete();
                }
            }
            return;
        }
        return;
    }

    public static void sendDocument(Context context, ResearchDocument document) {

        File source = new File(document.getFilepath());
        source.setReadable(true, false);

        if (source.exists()) {

            Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:")); // it's not ACTION_SEND
          //  intent.setType("text/plain");
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT, document.getTitle());
            intent.putExtra(Intent.EXTRA_TEXT, document.getTitle());
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(source));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            context.startActivity(intent);
        } else {
            Toast.makeText(context, context.getString(R.string.unable_to_prepare_file), Toast.LENGTH_LONG).show();
        }

    }
    public static void sendMail(Context context,String to, String subject, String content) {
       // Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
     //   intent.setType("text/plain");
        // prompts email clients only

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");

        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_EMAIL,  new String[]{to});
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(context, context.getString(R.string.no_mail_application), Toast.LENGTH_LONG).show();

        }

    }
    public static boolean copyFile(File source, File dest) {
        OutputStream myOutput = null;
        InputStream myInput = null;
        try {
            System.out.println("outFileName ::" + dest.getAbsolutePath());
            myInput = new FileInputStream(source);
            myOutput = new FileOutputStream(dest);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[2048];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            //Close the streams
            if (myOutput != null) {
                try {
                    myOutput.flush();
                    myOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            if (myInput != null) {
                try {
                    myInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static Intent newEmailIntent(Context context, String address) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", address, null));
        return Intent.createChooser(emailIntent, "Envoyer un email...");
    }

    public static Intent newPhoneIntent(Context context, String phoneNumber) {
        String nbr = phoneNumber.startsWith("tel:") ? phoneNumber : "tel:" + phoneNumber;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(nbr));
        return intent;
    }




    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }


    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
