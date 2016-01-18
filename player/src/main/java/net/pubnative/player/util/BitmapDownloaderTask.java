//
//  Copyright (c) 2014, Nexage, Inc.
//  All rights reserved.
//  Provided under BSD-3 license as follows:
//
//  Redistribution and use in source and binary forms, with or without modification,
//  are permitted provided that the following conditions are met:
//
//  Redistributions of source code must retain the above copyright notice, this
//  list of conditions and the following disclaimer.
//
//  Redistributions in binary form must reproduce the above copyright notice, this
//  list of conditions and the following disclaimer in the documentation and/or
//  other materials provided with the distribution.
//
//  Neither the name of Nexage nor the names of its
//  contributors may be used to endorse or promote products derived from
//  this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
//  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
//  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package net.pubnative.player.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

/**
 * Created by davidmartin on 11/01/16.
 */
public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = BitmapDownloaderTask.class.getName();
    public interface Listener {

        void onBitmapDownloaderFinished(Bitmap bitmap);
    }

    private Listener listener;

    public BitmapDownloaderTask setListener(Listener listener){

        this.listener = listener;
        return this;
    }


    @Override
    protected Bitmap doInBackground(String... params) {

        Bitmap result = null;

        if(params.length > 0) {

            try {

                String urlString = params[0];
                URL url = new URL(urlString);
                result = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (Exception e) {

                Log.e(TAG, "error: " + e);
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(Bitmap result) {

        if(listener != null) {

            listener.onBitmapDownloaderFinished(result);
        }
    }
}
