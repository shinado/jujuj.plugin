import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Administrator on 2015/11/17.
 */
public class ActivityForm extends JFrame{

    private OnInfoCompleteListener mOnInfoCompleteListener;
    private JPanel panel1;
    private JTextField activityInput;
    private JTextField urlInput;
    private JButton doneBtn;
    private JButton cancelBtn;
    private JCheckBox launcherCb;

    public ActivityForm(){
        super();

        setContentPane(panel1);
        pack();
        setVisible(true);
        doneBtn.addActionListener(mListener);
        cancelBtn.addActionListener(mListener);
    }

    private ActionListener mListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == doneBtn){
                String url = urlInput.getText();
                String activity = activityInput.getText();
                boolean isLauncher = launcherCb.isSelected();
                if (mOnInfoCompleteListener != null){
                    mOnInfoCompleteListener.onInfoCompleted(activity, url, isLauncher);
                }
                ActivityForm.this.dispose();
            }else if (source == cancelBtn){
                ActivityForm.this.dispose();
            }
        }
    };

    public void setOnInfoCompleteListener(OnInfoCompleteListener l){
        this.mOnInfoCompleteListener = l;
    }

    public interface OnInfoCompleteListener{
        public void onInfoCompleted(String activity, String url, boolean isLauncher);
    }
}
