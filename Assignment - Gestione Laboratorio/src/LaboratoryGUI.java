
import VitoBarra.JavaUtil.Other.ThreadPoolUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LaboratoryGUI extends JFrame
{
    private static LaboratoryGUI ins;
    private static final Color FreeColor = new Color(55, 55, 55);
    private static final Color TakenColor = new Color(162, 48, 48);
    private static final Color WhiteColor = new Color(255, 255, 255);
    List<JPanel> PCpos = new ArrayList<>();
    private JPanel Frame;
    private JPanel InternalGrid;

    private ExecutorService Drawer;

    LaboratoryGUI(String _title)
    {
        super(_title);
        ins = this;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(Frame);
        this.pack();
        this.setVisible(true);
        this.setSize(new Dimension(500, 500));

        Drawer = Executors.newSingleThreadExecutor();

        var layout = new GridLayout(4, 5);
        layout.setHgap(15);
        layout.setVgap(15);
        InternalGrid.setLayout(layout);

        for (int i = 0; i < 20; i++)
        {
            var PostPanel = new JPanel();
            PostPanel.setSize(80, 80);
            PostPanel.setVisible(true);
            PostPanel.setBackground(FreeColor);

            var label = new JLabel("");
            label.setForeground(FreeColor);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            var labelPosition = new JLabel(Integer.toString(i + 1));
            labelPosition.setHorizontalAlignment(SwingConstants.CENTER);
            labelPosition.setForeground(WhiteColor);

            PostPanel.setLayout(new GridLayout(3, 1));
            PostPanel.add(label);
            PostPanel.add(labelPosition);
            PCpos.add(PostPanel);
        }
        PCpos.forEach(x -> InternalGrid.add(x));

    }


    public synchronized static void TakePosition(String userName, int i)
    {
        if (ins == null) return;
        ins.Drawer.execute(() -> {
            var a = ins.PCpos.get(i);
            ((JLabel) a.getComponent(0)).setText("<html>" + userName + "</html>");
            a.setBackground(TakenColor);
        });

    }

    public  synchronized static void ReleasePosition(int i)
    {
        if (ins == null) return;
        ins.Drawer.execute(() -> {
            var a = ins.PCpos.get(i);
            ((JLabel) a.getComponent(0)).setText("");
            a.setBackground(FreeColor);
        });

    }


    public void CloseGUI()
    {
        ThreadPoolUtil.TryAwaitTermination( ins.Drawer,10, TimeUnit.SECONDS);
        dispose();
    }
}
