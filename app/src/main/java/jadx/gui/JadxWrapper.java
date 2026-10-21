package jadx.gui;

import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.JavaClass;
import jadx.api.JavaPackage;
import jadx.api.ResourceFile;

import android.app.ProgressDialog;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jadx 1.5.6 适配版 wrapper。
 * 与旧版差异：
 * - 不再使用 IJadxArgs，直接用 JadxArgs
 * - 没有 loadFile(File)，改为 args.setInputFile(...) + load()
 * - 没有 setOutputDir，改为 args.setOutDir(...)
 */
public class JadxWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(JadxWrapper.class);

    private final JadxDecompiler decompiler;
    private File openFile;

    public JadxWrapper(JadxArgs jadxArgs) {
        this.decompiler = new JadxDecompiler(jadxArgs);
    }

    public void openFile(File file) {
        this.openFile = file;
        try {
            decompiler.getArgs().setInputFile(file);
            decompiler.load();
        } catch (Exception e) {
            LOG.error("Error open file: {}", file, e);
        }
    }

    public void saveAll(final File dir, final ProgressDialog progressMonitor) {
        Runnable save = new Runnable() {
            @Override
            public void run() {
                try {
                    decompiler.getArgs().setOutDir(dir);
                    if (progressMonitor != null) {
                        progressMonitor.setIndeterminate(true);
                    }
                    // jadx 1.5.6 的 save() 内部自行调度并等待保存任务结束
                    decompiler.save();
                    if (progressMonitor != null) {
                        progressMonitor.cancel();
                    }
                    LOG.info("done");
                } catch (Exception e) {
                    LOG.error("Save failed", e);
                }
            }
        };
        new Thread(save).start();
    }

    public List<JavaClass> getClasses() {
        return decompiler.getClasses();
    }

    public List<JavaPackage> getPackages() {
        return decompiler.getPackages();
    }

    public List<ResourceFile> getResources() {
        return decompiler.getResources();
    }

    public File getOpenFile() {
        return openFile;
    }

    public JadxDecompiler getDecompiler() {
        return decompiler;
    }
}