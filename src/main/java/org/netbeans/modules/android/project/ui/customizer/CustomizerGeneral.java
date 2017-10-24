/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netbeans.modules.android.project.ui.customizer;

import org.netbeans.modules.android.project.AndroidGeneralData;
import com.android.sdklib.IAndroidTarget;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.android.core.sdk.DalvikPlatform;
import org.netbeans.modules.android.core.sdk.DalvikPlatformManager;
import org.netbeans.modules.android.project.AndroidProjectInfo;

/**
 *
 * @author radim
 */
public class CustomizerGeneral extends javax.swing.JPanel {

  private final AndroidGeneralData data;
  private final AndroidTargetTableModel tableModel;
  private final AndroidProjectInfo info;

  /** Creates new form CustomizerGeneral */
  public CustomizerGeneral(AndroidGeneralData data, AndroidProjectInfo info, DalvikPlatformManager platformManager) {
    this.data = data;
    this.info = info;
    initComponents();

    jTextFieldName.setText(data.getProjectName());
    jTextFieldPath.setText(data.getProjectDirPath());

    // init table
    jTableTargets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jTableTargets.setRowSelectionAllowed(true);
    jTableTargets.setColumnSelectionAllowed(false);
    List<IAndroidTarget> targets = new ArrayList<IAndroidTarget>();
    for (DalvikPlatform platform : platformManager.getPlatforms()) {
      targets.add(platform.getAndroidTarget());
    }
    tableModel = new AndroidTargetTableModel(targets);
    jTableTargets.setModel(tableModel);
    DalvikPlatform platform = data.getPlatform();
    if (platform != null) {
      int row = tableModel.getTargetRow(platform.getAndroidTarget());
      if (row != -1) {
        jTableTargets.setRowSelectionInterval(row, row);
      }
    }
    jTableTargets.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {
        IAndroidTarget target = tableModel.getTargetAt(jTableTargets.getSelectedRow());
        DalvikPlatform dPlatform = DalvikPlatformManager.getDefault().findPlatformForTarget(
            target != null ? target.hashString() : null);
        CustomizerGeneral.this.data.setPlatform(dPlatform);
      }
    });
    if (info.isNeedsFix()) {
      lblNeededAction.setText(info.getFixDescription());
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabelName = new javax.swing.JLabel();
    jTextFieldName = new javax.swing.JTextField();
    jLabelPath = new javax.swing.JLabel();
    jTextFieldPath = new javax.swing.JTextField();
    jLabelTarget = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    jTableTargets = new javax.swing.JTable();
    lblNeededAction = new javax.swing.JLabel();

    jLabelName.setLabelFor(jTextFieldName);
    jLabelName.setText(org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "CustomizerGeneral.jLabelName.text")); // NOI18N

    jTextFieldName.setEditable(false);
    jTextFieldName.setText(org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "CustomizerGeneral.jTextFieldName.text")); // NOI18N

    jLabelPath.setLabelFor(jTextFieldPath);
    jLabelPath.setText(org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "CustomizerGeneral.jLabelPath.text")); // NOI18N

    jTextFieldPath.setEditable(false);
    jTextFieldPath.setText(org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "CustomizerGeneral.jTextFieldPath.text")); // NOI18N

    jLabelTarget.setText(org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "CustomizerGeneral.jLabelTarget.text")); // NOI18N

    jTableTargets.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {

      }
    ));
    jTableTargets.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    jScrollPane1.setViewportView(jTableTargets);

    lblNeededAction.setText(org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "CustomizerGeneral.lblNeededAction.text")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
          .addComponent(lblNeededAction, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabelName)
              .addComponent(jLabelPath))
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jTextFieldPath, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
              .addComponent(jTextFieldName, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)))
          .addComponent(jLabelTarget, javax.swing.GroupLayout.Alignment.LEADING))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabelName)
          .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabelPath)
          .addComponent(jTextFieldPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabelTarget)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
        .addGap(18, 18, 18)
        .addComponent(lblNeededAction, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel jLabelName;
  private javax.swing.JLabel jLabelPath;
  private javax.swing.JLabel jLabelTarget;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTable jTableTargets;
  private javax.swing.JTextField jTextFieldName;
  private javax.swing.JTextField jTextFieldPath;
  private javax.swing.JLabel lblNeededAction;
  // End of variables declaration//GEN-END:variables

}
