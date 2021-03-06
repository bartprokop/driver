/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TibboDatabaseJDialog.java
 *
 * Created on Aug 26, 2011, 9:36:30 PM
 */
package name.prokop.bart.hardware.driver.rfid.tr610.v07;

import javax.swing.table.AbstractTableModel;
import name.prokop.bart.hardware.driver.rfid.tr610.db.TibboCard;
import name.prokop.bart.hardware.driver.rfid.tr610.db.TibboCardLogEntry;
import name.prokop.bart.hardware.driver.rfid.tr610.db.TibboDatabase;
import name.prokop.bart.hardware.driver.rfid.tr610.db.TibboHuman;

/**
 *
 * @author bart
 */
public class TibboDatabaseJDialog extends javax.swing.JDialog {

    private final TibboDatabase tibboDatabase;
    private boolean retVal = false;

    /** Creates new form TibboDatabaseJDialog */
    public TibboDatabaseJDialog(TibboDatabase tibboDatabase) {
        super();
        this.tibboDatabase = tibboDatabase;
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCards = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableHumans = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableLog = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        jLabel1.setText("Dzieci:");

        jTableCards.setModel(new CardsTableModel());
        jScrollPane1.setViewportView(jTableCards);

        jLabel2.setText("Karty");

        jTableHumans.setModel(new HumansTableModel());
        jScrollPane2.setViewportView(jTableHumans);

        jLabel3.setText("Zdarzenia:");

        jTableLog.setModel(new LogTableModel());
        jScrollPane3.setViewportView(jTableLog);

        jButton1.setText("Kontynuuj operacje");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Zamknij / Anuluj");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 771, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addContainerGap(539, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(256, 256, 256))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)
                                .addContainerGap())))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    dispose();
}//GEN-LAST:event_jButton2ActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    retVal = true;
    dispose();
}//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static boolean invoke(TibboDatabase tibboDatabase) {
        TibboDatabaseJDialog dialog = new TibboDatabaseJDialog(tibboDatabase);
        dialog.setVisible(true);
        return dialog.retVal;
    }

    public static void main(String... args) {
        EDzieckoApplet.main(args);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableCards;
    private javax.swing.JTable jTableHumans;
    private javax.swing.JTable jTableLog;
    // End of variables declaration//GEN-END:variables

    private class HumansTableModel extends AbstractTableModel {

        private TibboHuman[] values = tibboDatabase.getHumans().values().toArray(new TibboHuman[0]);

        public int getRowCount() {
            return values.length;
        }

        public int getColumnCount() {
            return 2;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return values[rowIndex].getPos();
                case 1:
                    return values[rowIndex].getName();
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Pozycja";
                case 1:
                    return "Imię i Nazwisko";
                default:
                    throw new IllegalStateException();
            }
        }
        
    }

    private class CardsTableModel extends AbstractTableModel {

        private TibboCard[] values = tibboDatabase.getCards().values().toArray(new TibboCard[0]);

        public int getRowCount() {
            return values.length;
        }

        public int getColumnCount() {
            return 4;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return values[rowIndex].getPos();
                case 1:
                    return values[rowIndex].getCardType();
                case 2:
                    return values[rowIndex].getSerialNumber();
                case 3:
                    return values[rowIndex].getHuman();
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Pozycja";
                case 1:
                    return "Typ karty";
                case 2:
                    return "Numer seryjny";
                case 3:
                    return "Osoba";
                default:
                    throw new IllegalStateException();
            }
        }
        
        
    }

    private class LogTableModel extends AbstractTableModel {

        private TibboCardLogEntry[] values = tibboDatabase.getCardLog().values().toArray(new TibboCardLogEntry[0]);

        public int getRowCount() {
            return values.length;
        }

        public int getColumnCount() {
            return 3;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return values[rowIndex].getPos();
                case 1:
                    return values[rowIndex].getTimestamp();
                case 2:
                    return values[rowIndex].getCard();
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Pozycja";
                case 1:
                    return "Czas";
                case 2:
                    return "Karta";
                default:
                    throw new IllegalStateException();
            }
        }
        
    }
}
