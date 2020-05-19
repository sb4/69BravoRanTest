package com.example.android.bravo69rantest;

import java.util.Arrays;

public class TableDef
{
    private String[][] arColumnDefs;
    private String tableName;
    private String createTableSql;
    private String dropTableSql;

    public TableDef(String tableName, String[][] arColumnDefs)
    {
        this.tableName = tableName;
        this.arColumnDefs = arColumnDefs;
    }

    public String[][] getArColumnDefs()
    {
        return arColumnDefs;
    }

    public void setArColumnDefs(String[][] arColumnDefs)
    {
        this.arColumnDefs = arColumnDefs;
    }

    public String getCreateTableSql()
    {
        String eol = C.EOL;

        if (createTableSql == null)
        {
            StringBuilder sbufCreateSql = new StringBuilder();

            sbufCreateSql.append("CREATE TABLE " + getTableName() + " (");
            String[][] arCols = getArColumnDefs();
            String strSuffix = eol;

            for (String[] arColDef : arCols)
            {
                sbufCreateSql.append(strSuffix);
                sbufCreateSql.append(arColDef[0] + " " + arColDef[1]);
                strSuffix = "," + eol;
            }

            sbufCreateSql.append(eol);
            sbufCreateSql.append(");");

            createTableSql = sbufCreateSql.toString();
        }

        return createTableSql.toString();
    }

    public String getDropTableSql()
    {
        if (dropTableSql == null)
        {
            dropTableSql = "DROP TABLE IF EXISTS " + getTableName() + ";";
        }

        return dropTableSql;
    }

    @Override
    public String toString()
    {
        return "TableDef{" +
                "arColumnDefs=" + Arrays.toString(arColumnDefs) +
                '}';
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

}
