/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.util.Properties;

import android.util.Log;

import com.funzio.pure2D.utils.PathUtils;

/**
 * @author long
 */
public class VersionedDownloadTaskGroup extends TaskGroup {

    private static final String TAG = VersionedDownloadTaskGroup.class.getSimpleName();
    private static final String VERSION_KEY = "version";

    private String mRemoteVersion;
    private String mLocalVersion;

    public VersionedDownloadTaskGroup() {
        super();
    }

    public VersionedDownloadTaskGroup(final int retryMax) {
        super(retryMax);
    }

    public VersionedDownloadTaskGroup(final int retryMax, final int retryDelay) {
        super(retryMax, retryDelay);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.TaskGroup#runTasks()
     */
    @Override
    protected boolean runTasks() {
        String remoteVersion = "";
        String localVersion = "";
        Properties remoteVersionProperties = null;
        Properties localVersionProperties = null;

        if (mRemoteVersion != null) {
            final URLLoadPropertiesTask remoteVersionTask = new URLLoadPropertiesTask(mRemoteVersion);
            if (remoteVersionTask.run()) {
                remoteVersionProperties = remoteVersionTask.getContent();
                if (remoteVersionProperties.containsKey(VERSION_KEY)) {
                    remoteVersion = remoteVersionProperties.getProperty(VERSION_KEY);
                }

                // now read the local version
                final ReadPropertiesFileTask localVersionTask = new ReadPropertiesFileTask(mLocalVersion);
                if (localVersionTask.run()) {
                    localVersionProperties = localVersionTask.getContent();
                    if (localVersionProperties.containsKey(VERSION_KEY)) {
                        localVersion = localVersionProperties.getProperty(VERSION_KEY);
                    }
                } else {
                    localVersionProperties = new Properties();
                }

                // update the version of the local file
                localVersionProperties.put(VERSION_KEY, remoteVersion);
            }
        }

        // compare the version
        final boolean hasNewVersion = !remoteVersion.equals(localVersion);
        boolean localVersionFileUpdated = hasNewVersion;

        Log.d(TAG, getClass().getSimpleName() + " versions: " + remoteVersion + " - " + localVersion + " -> " + hasNewVersion);

        final int size = mTasks.size();
        for (int i = 0; i < size; i++) {
            final Task task = mTasks.get(i);
            if (task instanceof DownloadTask) {
                final DownloadTask downloadTask = (DownloadTask) task;
                boolean fileHasSpecificVersion = false;

                // check for specific file's version
                if (remoteVersionProperties != null) {
                    final String urlWithoutParams = downloadTask.getURL().split("\\?")[0]; // strip out the url params
                    final String relativePathFromVersionFile = PathUtils.getRelativePath(mRemoteVersion, urlWithoutParams);
                    final String remoteFileVersion = remoteVersionProperties.getProperty(relativePathFromVersionFile, "");
                    if (!remoteFileVersion.equals("")) {
                        if (localVersionProperties != null) {
                            final String localFileVersion = localVersionProperties.getProperty(relativePathFromVersionFile, "");
                            // compare file versions
                            if (!remoteFileVersion.equals(localFileVersion)) {
                                fileHasSpecificVersion = true;
                                Log.d(TAG, relativePathFromVersionFile + " has new version: " + remoteFileVersion);
                            }

                            // put to the local version file
                            localVersionProperties.put(relativePathFromVersionFile, remoteFileVersion);
                        }

                        // flag to update the file
                        localVersionFileUpdated |= fileHasSpecificVersion;
                    }
                }

                // override if file changed
                downloadTask.setOverriding(hasNewVersion || fileHasSpecificVersion);
            }
        }

        // run the child tasks
        boolean success = super.runTasks();

        // save the version file
        if (localVersionFileUpdated) {
            Log.d(TAG, "Writing local version: " + mLocalVersion);
            final WritePropertiesFileTask writeVersionTask = new WritePropertiesFileTask(localVersionProperties, mLocalVersion, true);
            success &= writeVersionTask.run();
        }

        return success;
    }

    public String getRemoteVersion() {
        return mRemoteVersion;
    }

    public void setRemoteVersion(final String remoteURL) {
        mRemoteVersion = remoteURL;
    }

    public String getLocalVersion() {
        return mLocalVersion;
    }

    public void setLocalVersion(final String localPath) {
        mLocalVersion = localPath;
    }

}
