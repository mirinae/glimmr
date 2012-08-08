package com.bourke.glimmr.tasks;

import android.app.Activity;

import android.os.AsyncTask;

import android.util.Log;

import com.bourke.glimmr.activities.BaseActivity;
import com.bourke.glimmr.common.FlickrHelper;
import com.bourke.glimmr.event.Events.IPhotoListReadyListener;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.PhotoList;

import java.io.IOException;

import java.util.HashSet;
import java.util.Set;

public class LoadContactsPhotosTask extends AsyncTask<OAuth, Void, PhotoList> {

    private static final String TAG = "Glimmr/LoadContactsPhotosTask";

    private IPhotoListReadyListener mListener;
    private Activity mActivity;

    public LoadContactsPhotosTask(Activity a,
            IPhotoListReadyListener listener) {
        mListener = listener;
        mActivity = a;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ((BaseActivity) mActivity).showProgressIcon(true);
    }

    @Override
    protected PhotoList doInBackground(OAuth... arg0) {
        OAuthToken token = arg0[0].getToken();
        Flickr f = FlickrHelper.getInstance().getFlickrAuthed(
                token.getOauthToken(), token.getOauthTokenSecret());
        User user = arg0[0].getUser();

        try {
            /* Flickr api doc says max allowed is 50, but seems to allow
             * more... For some reason pagination doesn't seem to be an option.
             */
            int amountToFetch = 100;
            Set<String> extras = new HashSet<String>();
            extras.add("owner_name");
            extras.add("url_q");
            extras.add("url_l");
            extras.add("views");
            boolean justFriends = false;
            boolean singlePhoto = false;
            boolean includeSelf = false;
            return f.getPhotosInterface().getContactsPhotos(amountToFetch,
                    extras, justFriends, singlePhoto, includeSelf);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FlickrException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final PhotoList result) {
        if (result == null) {
            Log.e(TAG, "Error fetching contacts photos, result is null");
        }
        mListener.onPhotosReady(result);
        ((BaseActivity) mActivity).showProgressIcon(false);
    }
}