import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/11/17.
 */
public class LayoutSelector extends JFrame {

    private OnLayoutSelectListener mOnLayoutSelectListener;

    public LayoutSelector(final ArrayList<VirtualFile> children ) {
        super("List Source Demo");
        Container contentpane = getContentPane();
        contentpane.setLayout(new FlowLayout());

        String[] urls = new String[children.size()];
        int i=0;
        for (VirtualFile child : children){
            urls[i++] = child.getUrl();
        }
        String[] names = getNames(urls);
        final JBList list = new JBList(names);
        list.setSelectedIndex(0);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contentpane.add(new JBScrollPane(list));
        list.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                int i = list.getSelectedIndex();
                if (mOnLayoutSelectListener != null){
                    mOnLayoutSelectListener.onLayoutSelect(children.get(i));
                }
                LayoutSelector.this.dispose();
            }
        });
        setSize(300, 300);
        setVisible(true);
    }

    /**
     *
     * @param urls
     * @return
     */
    private String[] getNames(String[] urls){
        String[] ret = new String[urls.length];
        int i=0;
        for (String str:urls){
            int index = lastIndexOf(str, '/', 2);
            ret[i++] = str.substring(index);
        }
        return ret;

    }

    /**
     *
     * @param str
     * @param key
     * @param index the last index, 1 for last one, 2 for the second last one
     * @return
     */
    private int lastIndexOf(String str, char key, int index){
        for (int i=str.length()-1; i>=0; i--){
            if (str.charAt(i) == key){
                if (--index == 0){
                    return i;
                }
            }
        }
        return -1;
    }

    public void setOnLayoutSelectListener(OnLayoutSelectListener l){
        this.mOnLayoutSelectListener = l;
    }

    public interface OnLayoutSelectListener{
        /**
         *
         * @param file
         */
        public void onLayoutSelect(VirtualFile file);
    }
}
