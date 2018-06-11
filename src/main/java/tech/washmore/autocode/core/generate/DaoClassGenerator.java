package tech.washmore.autocode.core.generate;

import tech.washmore.autocode.core.config.ConfigManager;
import tech.washmore.autocode.model.Constants;
import tech.washmore.autocode.model.config.*;
import tech.washmore.autocode.model.enums.DataFileMethod;
import tech.washmore.autocode.model.enums.JavaDataType;
import tech.washmore.autocode.model.mysql.ColumnModel;
import tech.washmore.autocode.model.mysql.TableModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

import static tech.washmore.autocode.util.StringUtils.underline2Camel;

/**
 * @author Washmore
 * @version V1.0
 * @summary TODO
 * @Copyright (c) 2018, Lianjia Group All Rights Reserved.
 * @since 2018/6/11
 */
public class DaoClassGenerator {

    public static void generateDaos(List<TableModel> tableModels) {
        tableModels.forEach(t -> {
            try {
                generateDao(t);
                generateDaoExtends(t);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void generateDaoExtends(TableModel tm) throws IOException {
        Config config = ConfigManager.getConfig();
        Model modelConfig = config.getModel();
        DataFile dataFile = config.getDataFile();
        Dao dao = dataFile.getDao();
        Doc doc = config.getDoc();
        Project project = config.getProject();
        StringBuffer sb = new StringBuffer("");
        sb.append("package ").append(dao.getExtendsPackageName()).append(";").append(System.lineSeparator()).append(System.lineSeparator());
        sb.append("import ").append(dao.getBasePackageName()).append(".").append(tm.getClsName() + dao.getBaseSuffix()).append(";").append(System.lineSeparator());
        sb.append("/**").append(System.lineSeparator());
        sb.append(" * @author ").append(doc.getAuthor()).append(System.lineSeparator());
        sb.append(" * @version ").append(doc.getVersion()).append(System.lineSeparator());
        sb.append(" * @summary ").append(dao.getSummary() != null && dao.getSummary().length() > 0 ? dao.getSummary() : String.format(Constants.daoSummaryTemplate, tm.getTbComment(), tm.getTbName())).append(System.lineSeparator());
        sb.append(" * @Copyright ").append(doc.getCopyright()).append(System.lineSeparator());
        sb.append(" * @since ").append(LocalDate.now().toString()).append(System.lineSeparator());
        sb.append(" */").append(System.lineSeparator());
        sb.append("public interface ").append(tm.getClsName() + dao.getSuffix()).append(" extends ")
                .append(tm.getClsName() + dao.getBaseSuffix())
                .append(" {")
                .append(System.lineSeparator())
                .append(System.lineSeparator());


        sb.append("}");
        System.out.println(sb.toString());
        File dic = new File(project.getPath() + project.getJavaRoot() + dao.getExtendsPackagePath());
        if (!dic.exists()) {
            dic.mkdirs();
        }

        File file = new File(dic, tm.getClsName() + dao.getSuffix() + ".java");
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStream ops = new FileOutputStream(file);
        ops.write(sb.toString().getBytes());
        ops.flush();
        ops.flush();
    }

    private static void generateDao(TableModel tm) throws IOException {
        Config config = ConfigManager.getConfig();
        Model modelConfig = config.getModel();
        DataFile dataFile = config.getDataFile();
        Dao dao = dataFile.getDao();
        Doc doc = config.getDoc();
        Project project = config.getProject();
        StringBuffer sb = new StringBuffer("");
        sb.append("package ").append(dao.getBasePackageName()).append(";").append(System.lineSeparator()).append(System.lineSeparator());
        if (JavaDataType.时间.value.equals(tm.getPrimaryKey().getFieldType())) {
            sb.append("import java.util.Date;").append(System.lineSeparator());
        }
        sb.append("import ").append(modelConfig.getPackageName()).append(".").append(tm.getClsName()).append(";").append(System.lineSeparator());
        sb.append("/**").append(System.lineSeparator());
        sb.append(" * @author ").append(doc.getAuthor()).append(System.lineSeparator());
        sb.append(" * @version ").append(doc.getVersion()).append(System.lineSeparator());
        sb.append(" * @summary ").append(dao.getSummary() != null && dao.getSummary().length() > 0 ? dao.getSummary() : String.format(Constants.daoSummaryTemplate, tm.getTbComment(), tm.getTbName())).append(System.lineSeparator());
        sb.append(" * @Copyright ").append(doc.getCopyright()).append(System.lineSeparator());
        sb.append(" * @since ").append(LocalDate.now().toString()).append(System.lineSeparator());
        sb.append(" */").append(System.lineSeparator());
        sb.append("public interface ").append(tm.getClsName() + dao.getBaseSuffix()).append(" {")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        List<String> methods = dataFile.getMethodInclude();
        if (methods != null && methods.size() > 0) {
            for (String method : methods) {
                try {
                    switch (DataFileMethod.valueOf(method)) {
                        case insert:
                            sb.append(appendInsert(tm));
                            break;
                        case deleteByPrimaryKey:
                            sb.append(appendDeleteByPrimaryKey(tm));
                            break;
                        case updateByPrimaryKey:
                            sb.append(appendUpdateByPrimaryKey(tm));
                            break;
                        case selectByPrimaryKey:
                            sb.append(appendSelectByPrimaryKey(tm));
                            break;
                        case selectByParams:
                            sb.append(appendSelectByParams(tm));
                            if (sb.indexOf("import java.util.Map;") == -1) {
                                sb.insert(sb.lastIndexOf("import "), "import java.util.Map;" + System.lineSeparator());
                            }
                            sb.insert(sb.lastIndexOf("import "), "import java.util.List;" + System.lineSeparator());
                            break;
                        case countByParams:
                            sb.append(appendCountByParams(tm));
                            if (sb.indexOf("import java.util.Map;") == -1) {
                                sb.insert(sb.lastIndexOf("import "), "import java.util.Map;" + System.lineSeparator());
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        sb.append("}");
        System.out.println(sb.toString());
        File dic = new File(project.getPath() + project.getJavaRoot() + dao.getBasePackagePath());
        if (!dic.exists()) {
            dic.mkdirs();
        }

        File file = new File(dic, tm.getClsName() + dao.getBaseSuffix() + ".java");
        if (!file.exists()) {
            file.createNewFile();
        } else {
            file.delete();
            file.createNewFile();
        }
        OutputStream ops = new FileOutputStream(file);
        ops.write(sb.toString().getBytes());
        ops.flush();
        ops.flush();
    }

    private static String appendCountByParams(TableModel tm) {
        StringBuffer sb = new StringBuffer();
        sb.append("\tint countByParams(Map<String, Object> params);")
                .append(System.lineSeparator()).append(System.lineSeparator());
        return sb.toString();
    }

    private static String appendSelectByParams(TableModel tm) {
        StringBuffer sb = new StringBuffer();
        sb.append("\tList<").append(tm.getClsName()).append("> selectByParams(Map<String, Object> params);")
                .append(System.lineSeparator()).append(System.lineSeparator());
        return sb.toString();
    }

    private static String appendSelectByPrimaryKey(TableModel tm) {
        ColumnModel pk = tm.getPrimaryKey();
        if (pk == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\t").append(tm.getClsName()).append(" selectByPrimaryKey(")
                .append(pk.getFieldType()).append(" ")
                .append(pk.getFieldName())
                .append(");")
                .append(System.lineSeparator()).append(System.lineSeparator());
        return sb.toString();
    }

    private static String appendUpdateByPrimaryKey(TableModel tm) {
        ColumnModel pk = tm.getPrimaryKey();
        if (pk == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\tint updateByPrimaryKey(")
                .append(tm.getClsName()).append(" ")
                .append(underline2Camel(tm.getTbName(), false))
                .append(");")
                .append(System.lineSeparator()).append(System.lineSeparator());
        return sb.toString();
    }

    private static String appendDeleteByPrimaryKey(TableModel tm) {
        ColumnModel pk = tm.getPrimaryKey();
        if (pk == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\tint deleteByPrimaryKey(")
                .append(pk.getFieldType()).append(" ")
                .append(pk.getFieldName())
                .append(");")
                .append(System.lineSeparator()).append(System.lineSeparator());
        return sb.toString();
    }

    private static String appendInsert(TableModel tm) {
        StringBuffer sb = new StringBuffer();
        sb.append("\tint insert(")
                .append(tm.getClsName()).append(" ")
                .append(underline2Camel(tm.getTbName(), false))
                .append(");")
                .append(System.lineSeparator()).append(System.lineSeparator());
        return sb.toString();
    }
}
