package jadx.gui.treemodel;

import com.ppma.filemanager.R;

import jadx.api.JavaClass;
import jadx.api.JavaPackage;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class JPackage extends JNode implements Comparable<JPackage> {
	private static final long serialVersionUID = -4120718634156839804L;


	private String name;
	private final List<JClass> classes;
	private final List<JPackage> innerPackages = new ArrayList<JPackage>(1);

	public JPackage(JavaPackage pkg) {
		this.name = pkg.getName();
		List<JavaClass> javaClasses = pkg.getClasses();
		java.util.Set<String> names = new java.util.HashSet<String>(javaClasses.size());
		for (JavaClass javaClass : javaClasses) {
			try {
				names.add(javaClass.getName());
			} catch (Throwable ignore) {
			}
		}
		this.classes = new ArrayList<JClass>(javaClasses.size());
		for (JavaClass javaClass : javaClasses) {
			// 内部类（CardView$CardViewApi21 / CardView$1 ...）不单独列出，
			// 统一并入外部类的源码里显示（见 JClass#getContent）
			if (isInnerClass(javaClass, names)) {
				continue;
			}
			classes.add(new JClass(javaClass));
		}
		//update();
	}

	private static boolean isInnerClass(JavaClass javaClass, java.util.Set<String> names) {
		try {
			if (javaClass.isInner()) {
				return true;
			}
			String name = javaClass.getName();
			int idx = name.lastIndexOf('$');
			if (idx > 0 && names.contains(name.substring(0, idx))) {
				return true;
			}
		} catch (Throwable ignore) {
		}
		return false;
	}

	public JPackage(String name) {
		this.name = name;
		this.classes = new ArrayList<JClass>(1);
	}

	public final void update() {
		//removeAllChildren();
		for (JPackage pkg : innerPackages) {
			pkg.update();
			//add(pkg);
		}
		for (JClass cls : classes) {
			cls.update();
			//add(cls);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<JPackage> getInnerPackages() {
		return innerPackages;
	}

	public List<JClass> getClasses() {
		return classes;
	}

	@Override
	public int getIcon() {
		return R.mipmap.package_obj;
	}

	@Override
	public JClass getJParent() {
		return null;
	}

	@Override
	public int getLine() {
		return 0;
	}

	@Override
	public int compareTo(@NotNull JPackage o) {
		return name.compareTo(o.name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return name.equals(((JPackage) o).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String makeString() {
		return name;
	}

	@Override
	public String makeLongString() {
		return name;
	}
}
