/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model;

import ardis.model.conceitual.infoTable.ObjetoInfoTable;

/**
 *
 * @author Israel
 */
public abstract class ObjetoGraficoImpl extends ObjetoImpl implements ObjetoGrafico {




    @Override
    public Class getModelInfo() {

        return ObjetoInfoTable.class;
    }

    @Override
    public boolean isLimitLabelSize() {
        return true;
    }
}
