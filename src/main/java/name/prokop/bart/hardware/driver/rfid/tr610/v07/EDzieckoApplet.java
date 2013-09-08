/*
 * EDzieckoApplet.java
 *
 * Created on Aug 7, 2011, 11:21:43 PM
 */
package name.prokop.bart.hardware.driver.rfid.tr610.v07;

import java.util.Set;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;
import name.prokop.bart.hardware.driver.*;
import name.prokop.bart.hardware.driver.common.Network;
import name.prokop.bart.hardware.driver.common.Status;
import name.prokop.bart.hardware.driver.rfid.tr610.db.TibboDatabase;

/**
 *
 * @author bart
 */
public class EDzieckoApplet extends javax.swing.JApplet {

    private Driver driver;
    private String serverBase;
    private Long przedszkoleKeyId;
    private ServletDAO servletDAO = new ServletDAO(this);
    private Set<Device> driverDevices = new TreeSet<Device>();
    private BPDriverDevicesTableModel driverDevicesTableModel = new BPDriverDevicesTableModel();

    public static void main(String[] args) {
        javax.swing.JFrame app = new javax.swing.JFrame("Applet Container");
        app.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        app.setSize(800, 480);

        EDzieckoApplet applet = new EDzieckoApplet() {
            // When the local applet version is instantiated
            // we override getParameter and feed it the values we want.
            // This, of course, can also be done be extending the applet

            @Override
            public String getParameter(String name) {
                if (name.equalsIgnoreCase("serverBase")) {
                    return "http://e-dziecko.appspot.com";
                    //return "http://127.0.0.1:8888";
                }
                if (name.equalsIgnoreCase("przedszkoleKeyId")) {
                    // return "1"; // przedszkole Mrowla
                    // return "20001"; // przedszkole Trzciana
                    // return "27146"; // przedszkole Świlcza
                    return "28001"; // TT Soft - testowo
                    // return "29001"; // przedszkole Bratkowice
                }
                return null;
            }
        };
        // sure to make the applet do its thing
        applet.init();
        app.setLayout(new java.awt.BorderLayout());
        // include it as a component.  local testing can now start
        app.getContentPane().add(applet, java.awt.BorderLayout.CENTER);
        app.setVisible(true);
    }

    /**
     * Initializes the applet EDzieckoApplet
     */
    @Override
    public void init() {
        initLocal();
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {

                public void run() {
                    initComponents();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void initLocal() {
        serverBase = getParameter("serverBase");
        if (!serverBase.endsWith("/")) {
            serverBase = serverBase + "/";
        }
        System.out.println("serverBase: " + serverBase);
        if (getParameter("przedszkoleKeyId") != null) {
            przedszkoleKeyId = Long.parseLong(getParameter("przedszkoleKeyId"));
        }
        System.out.println("przedszkoleKeyId: " + przedszkoleKeyId);
        try {
//            BPDriver.runInBackground(true);
//            BPDriverBusHolder.getInstance().addBus(new TTDevice0006Server());
//            BrokerEvent.getInstance().addListener(this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldPrzedszkoleId = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonDropDatabase = new javax.swing.JButton();
        jButtonClearLog = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jButton3 = new javax.swing.JButton();

        jButton1.setText("Wyślij do chmury");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Zaprogramuj z chmury");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Identyfikatory"));

        jLabel1.setText("Lokalny adres IP");

        jTextField1.setEditable(false);
        jTextField1.setText(Network.getLocalIp());

        jLabel2.setText("URL aplikacji");

        jTextField2.setEditable(false);
        jTextField2.setText(serverBase);

        jLabel3.setText("ID placówki");

        jTextFieldPrzedszkoleId.setEditable(false);
        jTextFieldPrzedszkoleId.setText(przedszkoleKeyId+"");
        jTextFieldPrzedszkoleId.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldPrzedszkoleIdMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                    .addComponent(jTextFieldPrzedszkoleId))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldPrzedszkoleId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Urządzenia wykryte przez BP Driver"));

        jTable1.setModel(driverDevicesTableModel);
        jScrollPane1.setViewportView(jTable1);

        jButtonDropDatabase.setText("Kasuj pamięć");
        jButtonDropDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDropDatabaseActionPerformed(evt);
            }
        });

        jButtonClearLog.setText("Czyść dziennik");
        jButtonClearLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearLogActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Postęp operacji"));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton3.setText("Konsola");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDropDatabase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonClearLog)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButtonDropDatabase)
                    .addComponent(jButtonClearLog)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Wybierz najpierw urządzenie.");
                return;
            }
            String deviceAddress = (String) driverDevicesTableModel.getValueAt(selectedRow, 0);
            final TTDevice0006v07 device = (TTDevice0006v07) driver.getDevice(deviceAddress);
            final TibboDatabase db = device.retrieveDatabase();
            if (TibboDatabaseJDialog.invoke(db)) {
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            Status status = servletDAO.sendDatabase(db);
                            if (status.isSuccessful()) {
                                device.databaseClearTable("CardLog");
                                JOptionPane.showMessageDialog(rootPane, "Dane wysłane poprawnie.\nDziennik zdarzeń wyczyszczony.");
                            } else {
                                JOptionPane.showMessageDialog(rootPane, "Problem: " + status.getDescription());
                            }
                        } finally {
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
            e.printStackTrace(System.err);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Wybierz najpierw urządzenie.");
                return;
            }
            TibboDatabase db = servletDAO.retrieveDatabase();
            if (TibboDatabaseJDialog.invoke(db)) {
                String deviceAddress = (String) driverDevicesTableModel.getValueAt(selectedRow, 0);
                TTDevice0006v07 device = (TTDevice0006v07) driver.getDevice(deviceAddress);
                device.databaseUpload(db);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
            e.printStackTrace(System.err);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

private void jButtonDropDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDropDatabaseActionPerformed
    try {
        if (JOptionPane.showConfirmDialog(this, "Czy na 100% chcesz skasować pamięć?") != 0) {
            return;
        }
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Wybierz najpierw urządzenie.");
            return;
        }
        String deviceAddress = (String) driverDevicesTableModel.getValueAt(selectedRow, 0);
        TTDevice0006v07 device = (TTDevice0006v07) driver.getDevice(deviceAddress);
        device.databaseDrop();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(rootPane, e);
    }
}//GEN-LAST:event_jButtonDropDatabaseActionPerformed

private void jButtonClearLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearLogActionPerformed
    try {
        if (JOptionPane.showConfirmDialog(this, "Czy na 100% chcesz skasować dziennik zdarzeń?") != 0) {
            return;
        }
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        String deviceAddress = (String) driverDevicesTableModel.getValueAt(selectedRow, 0);
        TTDevice0006v07 device = (TTDevice0006v07) driver.getDevice(deviceAddress);
        device.databaseClearTable("CardLog");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(rootPane, e);
    }
}//GEN-LAST:event_jButtonClearLogActionPerformed

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    try {
//        BartConsole.init();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(rootPane, e);
    }
}//GEN-LAST:event_jButton3ActionPerformed

private void jTextFieldPrzedszkoleIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldPrzedszkoleIdMouseClicked
    if (evt.isShiftDown()) {
        jTextFieldPrzedszkoleId.setEditable(true);
        przedszkoleKeyId = Long.parseLong(jTextFieldPrzedszkoleId.getText());
    }
}//GEN-LAST:event_jTextFieldPrzedszkoleIdMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButtonClearLog;
    private javax.swing.JButton jButtonDropDatabase;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextFieldPrzedszkoleId;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables

    public String getServerBase() {
        return serverBase;
    }

    public Long getPrzedszkoleKeyId() {
        return przedszkoleKeyId;
    }

    private class BPDriverDevicesTableModel extends AbstractTableModel {

        public int getRowCount() {
            return driverDevices.size();
        }

        public int getColumnCount() {
            return 3;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Device[] array = driverDevices.toArray(new Device[driverDevices.size()]);
            switch (columnIndex) {
                case 0:
                    return array[rowIndex].getDeviceAddress();
                case 1:
                    return array[rowIndex].getDeviceInfo();
                case 2:
                    return array[rowIndex].getDeviceDescription();
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Adres logiczny";
                case 1:
                    return "Stan urządzenia";
                case 2:
                    return "Opis urządzenia";
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public void bpdriverEventOccured(Event e) {
        System.out.print(e);
        if (e instanceof DeviceDetectedEvent) {
            driverDevices.add(e.getSourceDevice());
            driverDevicesTableModel.fireTableStructureChanged();
        }
        if (e instanceof DeviceDropEvent) {
            driverDevices.remove(e.getSourceDevice());
            driverDevicesTableModel.fireTableStructureChanged();
        }
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }
}
