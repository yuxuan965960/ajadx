package jadx.gui.treemodel;

import com.ppma.filemanager.R;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jadx.api.ICodeInfo;
import jadx.api.ResourceFile;
import jadx.api.ResourceType;
import jadx.core.xmlgen.ResContainer;

/**
 * jadx 1.5.6 适配：CodeWriter 已被 ICodeInfo/ICodeWriter 取代，
 * 这里直接保存资源文本内容。
 */
public class JResource extends JNode implements Comparable<JResource> {

    private static final long serialVersionUID = -201018424302612434L;

    private static final int ROOT_ICON = R.mipmap.cf_obj;
    private static final int FOLDER_ICON = R.mipmap.folder;
    private static final int FILE_ICON = R.mipmap.file_obj;
    private static final int MANIFEST_ICON = R.mipmap.template_obj;
    private static final int ERROR_ICON = R.mipmap.error_co;

    public enum JResType {
        ROOT,
        DIR,
        FILE
    }

    private final String name;
    private final String shortName;
    private final List<JResource> files = new ArrayList<JResource>(1);
    private final JResType type;
    private final ResourceFile resFile;
    private boolean loaded;
    private String content;
    private Map<Integer, Integer> lineMapping;

    public JResource(ResourceFile resFile, String name, JResType type) {
        this(resFile, name, name, type);
    }

    public JResource(ResourceFile resFile, String name, String shortName, JResType type) {
        this.resFile = resFile;
        this.name = name;
        this.shortName = shortName;
        this.type = type;
    }

    /** 直接带内容构造（用于资源子文件） */
    public JResource(String name, String shortName, JResType type, String content) {
        this.resFile = null;
        this.name = name;
        this.shortName = shortName;
        this.type = type;
        this.content = content;
        this.loaded = true;
    }

    public String getName() {
        return name;
    }

    public List<JResource> getFiles() {
        return files;
    }

    public String getContent() {
        if (!loaded && resFile != null && type == JResType.FILE) {
            loaded = true;
            if (isSupportedForView(resFile.getType())) {
                ResContainer rc = resFile.loadContent();
                if (rc != null) {
                    addSubFiles(rc, this, 0);
                }
            }
        }
        return content;
    }

    protected void addSubFiles(ResContainer rc, JResource root, int depth) {
        ICodeInfo codeInfo = rc.getText();
        if (codeInfo != null) {
            String str = codeInfo.getCodeStr();
            if (depth == 0) {
                root.content = str;
            } else {
                String name = rc.getName();
                String[] path = name.split("/");
                String shortName = path.length == 0 ? name : path[path.length - 1];
                addPath(path, root, new JResource(name, shortName, JResType.FILE, str));
            }
        }
        List<ResContainer> subFiles = rc.getSubFiles();
        if (subFiles != null && !subFiles.isEmpty()) {
            for (ResContainer subFile : subFiles) {
                addSubFiles(subFile, root, depth + 1);
            }
        }
    }

    private static void addPath(String[] path, JResource root, JResource jResource) {
        if (path.length == 1) {
            root.getFiles().add(jResource);
            return;
        }
        int last = path.length - 1;
        for (int i = 0; i <= last; i++) {
            String f = path[i];
            if (i == last) {
                root.getFiles().add(jResource);
            } else {
                root = getResDir(root, f);
            }
        }
    }

    private static JResource getResDir(JResource root, String dirName) {
        for (JResource file : root.getFiles()) {
            if (file.getName().equals(dirName)) {
                return file;
            }
        }
        JResource resDir = new JResource(null, dirName, JResType.DIR);
        root.getFiles().add(resDir);
        return resDir;
    }

    @Override
    public Integer getSourceLine(int line) {
        if (lineMapping == null) {
            return null;
        }
        return lineMapping.get(line);
    }

    @Override
    public String getSyntaxName() {
        if (resFile == null) {
            return null;
        }
        switch (resFile.getType()) {
            case CODE:
                return super.getSyntaxName();
            case MANIFEST:
            case XML:
                return SyntaxConstants.SYNTAX_STYLE_XML;
            default:
                break;
        }
        String syntax = getSyntaxByExtension(resFile.getOriginalName());
        if (syntax != null) {
            return syntax;
        }
        return super.getSyntaxName();
    }

    private String getSyntaxByExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot == -1) {
            return null;
        }
        String ext = name.substring(dot + 1);
        if (ext.equals("js")) {
            return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
        }
        if (ext.equals("css")) {
            return SyntaxConstants.SYNTAX_STYLE_CSS;
        }
        if (ext.equals("html")) {
            return SyntaxConstants.SYNTAX_STYLE_HTML;
        }
        return null;
    }

    @Override
    public int getIcon() {
        switch (type) {
            case ROOT:
                return ROOT_ICON;
            case DIR:
                return FOLDER_ICON;
            case FILE:
                ResourceType resType = resFile.getType();
                if (resType == ResourceType.MANIFEST) {
                    return MANIFEST_ICON;
                }
                return FILE_ICON;
            default:
                return FILE_ICON;
        }
    }

    public static boolean isSupportedForView(ResourceType type) {
        switch (type) {
            case CODE:
            case FONT:
            case LIB:
                return false;
            case MANIFEST:
            case XML:
            case ARSC:
            case IMG:
            case UNKNOWN:
                return true;
            default:
                return true;
        }
    }

    public ResourceFile getResFile() {
        return resFile;
    }

    @Override
    public JClass getJParent() {
        return null;
    }

    @Override
    public int compareTo(JResource o) {
        return name.compareTo(o.name);
    }

    @Override
    public String makeString() {
        return shortName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return name.equals(((JResource) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}