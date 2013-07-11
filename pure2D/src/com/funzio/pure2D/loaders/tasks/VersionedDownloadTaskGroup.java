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
        URLLoadPropertiesTask remoteVersionTask = null;
        ReadPropertiesFileTask localVersionTask = null;
        Properties localVersionProperties = null;

        if (mRemoteVersion != null) {
            remoteVersionTask = new URLLoadPropertiesTask(mRemoteVersion);
            if (remoteVersionTask.run()) {
                if (remoteVersionTask.getContent().containsKey(VERSION_KEY)) {
                    remoteVersion = remoteVersionTask.getContent().getProperty(VERSION_KEY);
                }

                // now read the local version
                localVersionTask = new ReadPropertiesFileTask(mLocalVersion);
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
                if (remoteVersionTask != null && remoteVersionTask.getContent() != null) {
                    final String relativePathFromVersionFile = PathUtils.getRelativePath(mRemoteVersion, downloadTask.getURL());
                    final String remoteFileVersion = remoteVersionTask.getContent().getProperty(relativePathFromVersionFile, "");
                    if (!remoteFileVersion.equals("")) {
                        if (localVersionTask != null) {
                            final String localFileVersion = localVersionTask.getContent().getProperty(relativePathFromVersionFile, "");
                            // compare file versions
                            if (!remoteFileVersion.equals(localFileVersion)) {
                                fileHasSpecificVersion = true;
                                Log.d(TAG, relativePathFromVersionFile + " has new version: " + remoteFileVersion);
                            }
                        }

                        // put to the local version file
                        localVersionProperties.put(relativePathFromVersionFile, remoteFileVersion);
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
