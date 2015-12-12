/**
 * Created by Administrator on 2015/11/17.
 */
public class ActivityGenerator {

    /**
     *
     * @param packageName  the package name of this activity file
     * @param appPackage   the package of application
     * @param activityName the name of this activity
     * @param url          the url of data stored
     * @param layout       the layout file with .xml
     * @return
     */
    public static String generateActivity(String packageName, String appPackage, String activityName, String beanClass){
        StringBuilder sb = new StringBuilder();
        if (packageName != null){
            sb.append("package " + packageName + ";");
            sb.append("\n");
        }

        sb.append("import " + appPackage + ".R;");
        sb.append("\n");

        sb.append("import android.app.Activity; \n");
        sb.append("import android.os.Bundle; \n");
        sb.append("import framework.core.Jujuj;\n");
        sb.append("import framework.inj.entity.MutableEntity;\n");
        sb.append("\n");

        sb.append("public class " + activityName + " extends Activity{");
        sb.append("\n");

        sb.append("    @Override");
        sb.append("\n");
        sb.append("    protected void onCreate(Bundle savedInstanceState) {");
        sb.append("\n");
        sb.append("        super.onCreate(savedInstanceState);");
        sb.append("\n");
        sb.append("        Jujuj.getInstance().inject(this, new MutableEntity(new "+ beanClass +"()));");
        sb.append("\n");
        sb.append("    }");
        sb.append("\n");
        sb.append("}");
        sb.append("\n");

        return sb.toString();
    }

    /**
     *
     * @param appPackage
     * @param activityName
     * @param src the physical path
     */
    public static void writeActivityToTarget(String appPackage, String activityName, String beanName, String src){
        String packageName = getPackage(src);
        String activity = generateActivity(packageName, appPackage, activityName, beanName);
        String fileName = src + "/" + activityName + ".java";
        FileIO.write(fileName, activity);
    }

    /**
     * get package name from url of
     * @param url physical url of selected file
     * @return
     */
    public static String getPackage(String url){
        //in Android project, the src is in java folder
        int start = url.indexOf("java");
        if (start < 0){
            return null;
        }
        //remove java/
        String packageName = url.substring(start+5, url.length());
        packageName = packageName.replace("/", ".");
        return packageName;
    }
}
