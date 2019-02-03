package com.checkin.app.checkin.Utility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.checkin.app.checkin.BuildConfig;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {
    private RequestBody mDelegate;
    private UploadCallbacks mListener;

    private boolean mUpdateProgressFlag = !BuildConfig.DEBUG;

    public ProgressRequestBody(RequestBody delegate, final UploadCallbacks listener) {
        this.mDelegate = delegate;
        this.mListener = listener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mDelegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return mDelegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        ProgressSink progressSink = new ProgressSink(sink);
        BufferedSink bufferedSink = Okio.buffer(progressSink);

        mDelegate.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    final class ProgressSink extends ForwardingSink {
        private long bytesUploaded = 0;
        private int lastProgress = 0;

        ProgressSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesUploaded += byteCount;

            if (mUpdateProgressFlag) {
                int progress = (int) (100 * bytesUploaded / contentLength());
                if (progress > lastProgress + 1)
                    mListener.onProgressUpdate(progress);
                lastProgress = progress;
            }

            mUpdateProgressFlag = true;
        }
    }

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
        void onSuccess();
        void onFailure();
    }
}