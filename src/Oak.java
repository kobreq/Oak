import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Oak extends JFrame {
    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private JTextArea fileInfoArea;
    private JButton deleteButton;

    public Oak() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new File("/"));
        treeModel = new DefaultTreeModel(root);
        fileTree = new JTree(treeModel);
        fileTree.addTreeSelectionListener(e -> showFileInfo(e.getPath()));
        JScrollPane treeScroll = new JScrollPane(fileTree);
        add(treeScroll, BorderLayout.WEST);

        fileInfoArea = new JTextArea();
        fileInfoArea.setEditable(false);
        JScrollPane infoScroll = new JScrollPane(fileInfoArea);
        add(infoScroll, BorderLayout.CENTER);

        deleteButton = new JButton("Delete the selected file or directory");
        deleteButton.addActionListener(new DeleteFileListener());
        add(deleteButton, BorderLayout.SOUTH);

        loadFileTree(root);
    }

    private void loadFileTree(DefaultMutableTreeNode node) {
        File file = (File) node.getUserObject();
        File[] files = file.listFiles();
        
        if (files != null) {
            for (File child : files) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                node.add(childNode);
                if (child.isDirectory()) {
                    loadFileTree(childNode);
                }
            }
        }
    }

    private void showFileInfo(TreePath path) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        File file = (File) node.getUserObject();
        StringBuilder info = new StringBuilder();
        info.append("Name: ").append(file.getName()).append("\n");
        info.append("Path: ").append(file.getPath()).append("\n");
        info.append("Size: ").append(file.length()).append(" bytes\n");
        info.append("Directory: ").append(file.isDirectory()).append("\n");
        fileInfoArea.setText(info.toString());
    }

    private class DeleteFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            TreePath selectedPath = fileTree.getSelectionPath();
            if (selectedPath == null) {
                JOptionPane.showMessageDialog(Oak.this, "Please select a file or directory to delete.");
                return;
            }
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            File file = (File) selectedNode.getUserObject();

            int confirm = JOptionPane.showConfirmDialog(Oak.this, 
                    "Are you sure you want to delete " + file.getName() + "?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (file.delete()) {
                    treeModel.removeNodeFromParent(selectedNode);
                    fileInfoArea.setText("");
                    JOptionPane.showMessageDialog(Oak.this, "File/Directory deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(Oak.this, "Failed to delete the file or directory.");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Oak manager = new Oak();
            manager.setVisible(true);
        });
    }
}