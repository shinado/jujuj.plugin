
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/11/17.
 */
public class CreateActivityAction extends AnAction {

    private String mApplicationPackage;
    private VirtualFile mLayoutSelected;
//    private String mUrl;
    private VirtualFile mFileSelected;
    private VirtualFile mAndroidManifest;

    public void actionPerformed(AnActionEvent e) {

        Project project = e.getProject();
        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);

        mFileSelected = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);

        VirtualFile module = findRootProject(rootManager.getContentRoots(), mFileSelected);
        ArrayList<VirtualFile> layouts = scanModule(module);
        showSelection(layouts);
    }

    /**
     * find the root project target belongs to
     * @param roots
     * @param target
     * @return
     */
    private VirtualFile findRootProject(VirtualFile[] roots, VirtualFile target){
        for (VirtualFile root : roots){
            if (target.getUrl().contains(root.getUrl())){
                return root;
            }
        }
        return null;
    }

    /**
     * look for layout directory
     * step 1
     * @param file
     */
    private ArrayList<VirtualFile> scanModule(VirtualFile file){
        ArrayList<VirtualFile> layouts = new ArrayList<VirtualFile>();

        if (file.isDirectory()){
            layouts.addAll(scanForLayout(file));
            VirtualFile[] children = file.getChildren();
            for (VirtualFile child : children){
                layouts.addAll(scanModule(child));
            }
        }else {
            scanForManifest(file);
        }
        return layouts;
    }

    private ArrayList<VirtualFile> scanForLayout(VirtualFile file){
        String fileUrl = file.getUrl();
        ArrayList<VirtualFile> layouts = new ArrayList<VirtualFile>();
        if (fileUrl.contains("layout")){
            if (!fileUrl.contains("build/intermediates") ) {
                VirtualFile[] children = file.getChildren();
                for (VirtualFile child : children) {
                    layouts.add(child);
                }
            }
        }
        return layouts;
    }

    private void scanForManifest(VirtualFile file){
        if (file.getName().contains("AndroidManifest.xml")){
            mAndroidManifest = file;
            mApplicationPackage = ManifestHandler.getPackageFromManifest(file.getPresentableUrl());
        }
    }

    //show selection of layout
    private void showSelection(ArrayList<VirtualFile> children){
        LayoutSelector selector = new LayoutSelector(children);
        selector.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selector.setOnLayoutSelectListener(new LayoutSelector.OnLayoutSelectListener() {
            @Override
            public void onLayoutSelect(VirtualFile file) {
                mLayoutSelected = file;
                setInfo();
            }
        });
    }

    //set activity name and url and if it is launcher
    private void setInfo(){
        ActivityForm form = new ActivityForm();
        form.setOnInfoCompleteListener(new ActivityForm.OnInfoCompleteListener() {
            @Override
            public void onInfoCompleted(String activity, String requestUrl, boolean isLauncher) {
                //create activity file

                String src = getPhysicalUrl(mFileSelected);
                String layoutUrl = getPhysicalUrl(mLayoutSelected);

                String packageName = ActivityGenerator.getPackage(src);
                ManifestHandler.addActivityToManifest(getPhysicalUrl(mAndroidManifest), packageName + "." + activity, isLauncher);
                String beanClass = LayoutHandler.writeToBeanClass(mApplicationPackage, src, layoutUrl, requestUrl);
                ActivityGenerator.writeActivityToTarget(mApplicationPackage, activity, beanClass, src);
            }

        });
    }


    private String getPhysicalUrl(VirtualFile file){
        return file.getUrl().replace("file://", "");
    }
}
