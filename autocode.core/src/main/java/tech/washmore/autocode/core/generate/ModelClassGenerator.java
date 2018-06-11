package tech.washmore.autocode.core.generate;

import tech.washmore.autocode.core.config.ConfigManager;
import tech.washmore.autocode.model.config.Config;
import tech.washmore.autocode.model.config.Doc;
import tech.washmore.autocode.model.config.Model;
import tech.washmore.autocode.model.config.Project;
import tech.washmore.autocode.model.enums.JavaDataType;
import tech.washmore.autocode.model.mysql.ColumnModel;
import tech.washmore.autocode.model.mysql.TableModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static tech.washmore.autocode.util.StringUtils.underline2Camel;

/**
 * @author Washmore
 * @version V1.0
 * @summary TODO
 * @Copyright (c) 2018, Lianjia Group All Rights Reserved.
 * @since 2018/6/11
 */
public class ModelClassGenerator {

    public static void generateModels(List<TableModel> tableModels) {
        tableModels.forEach(t -> {
            try {
                generateModel(t);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 生成Model文件
     *
     * @param tm
     * @throws IOException
     */
    private static void generateModel(TableModel tm) throws IOException {
        Config config = ConfigManager.getConfig();
        Model modelConfig = config.getModel();
        Doc doc = config.getDoc();
        Project project = config.getProject();
        StringBuffer sb = new StringBuffer("");
        //添加包名
        sb.append("package ").append(modelConfig.getPackageName()).append(";").append(System.lineSeparator()).append(System.lineSeparator());
        //引入依赖项
        sb.append("import java.io.Serializable;").append(System.lineSeparator());
        if (tm.getColumns().stream().map(ColumnModel::getFieldType).anyMatch(ft -> JavaDataType.时间.value.equals(ft))) {
            sb.append("import java.util.Date;").append(System.lineSeparator());
        }
        //添加类文件注释
        sb.append("/**").append(System.lineSeparator());
        sb.append(" * @author ").append(doc.getAuthor()).append(System.lineSeparator());
        sb.append(" * @version ").append(doc.getVersion()).append(System.lineSeparator());
        sb.append(" * @summary ").append(modelConfig.getSummary() != null && modelConfig.getSummary().length() > 0 ? modelConfig.getSummary() : tm.getTbComment()).append(System.lineSeparator());
        sb.append(" * @Copyright (c) 2018, Lianjia Group All Rights Reserved.").append(System.lineSeparator());
        sb.append(" * @since ").append(LocalDate.now().toString()).append(System.lineSeparator());
        sb.append(" */").append(System.lineSeparator());


        sb.append("public class ").append(tm.getClsName()).append(" implements Serializable ").append("{")
                .append(System.lineSeparator());
        //序列化id
        sb.append("\tprivate static final long serialVersionUID = ").append(new Random().nextLong()).append("L;")
                .append(System.lineSeparator());
        if (tm.getColumns() != null && tm.getColumns().size() > 0) {
            //属性列表
            for (ColumnModel cm : tm.getColumns()) {
                sb.append("\t/**").append(System.lineSeparator());
                sb.append("\t * ").append(cm.getComment()).append(" 默认值:").append(cm.getDefaultValue())
                        .append(System.lineSeparator());
                sb.append("\t */").append(System.lineSeparator());
                sb.append("\t").append("private ").append(cm.getFieldType()).append(" ").append(cm.getFieldName())
                        .append(";").append(System.lineSeparator());
            }
            sb.append(System.lineSeparator());
            //重写toString方法
            if (modelConfig.getToString().booleanValue()) {
                sb.append("\t@Override").append(System.lineSeparator());
                sb.append("\tpublic String toString() {").append(System.lineSeparator());
                sb.append("\t\treturn \"").append(tm.getClsName()).append("{\" +").append(System.lineSeparator());
                for (int i = 0; i < tm.getColumns().size(); i++) {
                    ColumnModel cm = tm.getColumns().get(i);
                    sb.append("\t\t\t\t\"");
                    if (i != 0) {
                        sb.append(", ");
                    }
                    sb.append(tm.getColumns().get(i).getFieldName()).append("=");
                    if (JavaDataType.字符串.value.equals(cm.getFieldType())) {
                        sb.append("\\'");
                    }
                    sb.append("\" + ").append(cm.getFieldName()).append(" +");
                    if (JavaDataType.字符串.value.equals(cm.getFieldType())) {
                        sb.append(" \"\\'\" +");
                    }
                    sb.append(System.lineSeparator());

                }
                sb.append("\t\t\t\"}\";").append(System.lineSeparator());
                sb.append("\t}").append(System.lineSeparator());

            }
            sb.append(System.lineSeparator());
            //getter,setter
            for (ColumnModel cm : tm.getColumns()) {
                if (modelConfig.getVisitorWithDoc().booleanValue()) {
                    sb.append("\t/**").append(System.lineSeparator());
                    sb.append("\t * ").append(cm.getComment()).append(" 默认值:").append(cm.getDefaultValue())
                            .append(System.lineSeparator());
                    sb.append("\t */").append(System.lineSeparator());
                }
                sb.append("\t").append("public void set").append(underline2Camel(cm.getColumnName(), true)).append("(")
                        .append(cm.getFieldType()).append(" ").append(cm.getFieldName()).append(") {")
                        .append(System.lineSeparator());
                sb.append("\t\t").append("this.").append(cm.getFieldName()).append(" = ").append(cm.getFieldName())
                        .append(";").append(System.lineSeparator());
                sb.append("\t}").append(System.lineSeparator());

                if (modelConfig.getVisitorWithDoc().booleanValue()) {
                    sb.append("\t/**").append(System.lineSeparator());
                    sb.append("\t * ").append(cm.getComment()).append(" 默认值:").append(cm.getDefaultValue())
                            .append(System.lineSeparator());
                    sb.append("\t */").append(System.lineSeparator());
                }
                sb.append("\t").append("public ").append(cm.getFieldType()).append(" get")
                        .append(underline2Camel(cm.getColumnName(), true)).append("() {").append(System.lineSeparator());
                sb.append("\t\t").append("return this.").append(cm.getFieldName()).append(";")
                        .append(System.lineSeparator());
                sb.append("\t}").append(System.lineSeparator());
            }
        }
        sb.append("}");
        System.out.println(sb.toString());
        //生成类文件
        File dic = new File(project.getPath() + project.getJavaRoot() + modelConfig.getPackagePath());
        if (!dic.exists()) {
            dic.mkdirs();
        }

        File file = new File(dic, tm.getClsName() + ".java");
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
}