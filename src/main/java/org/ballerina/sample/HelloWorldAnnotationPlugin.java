package org.ballerina.sample;

import org.ballerinalang.compiler.plugins.AbstractCompilerPlugin;
import org.ballerinalang.compiler.plugins.SupportedAnnotationPackages;
import org.ballerinalang.util.codegen.AnnAttachmentInfo;
import org.ballerinalang.util.codegen.PackageInfo;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.codegen.ProgramFileReader;
import org.ballerinalang.util.codegen.ServiceInfo;
import org.ballerinalang.util.diagnostic.DiagnosticLog;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hello world Annotation Plugin.
 */
@SupportedAnnotationPackages(
        value = {"ballerina.hello"}
)
public class HelloWorldAnnotationPlugin extends AbstractCompilerPlugin {

    DiagnosticLog dlog;
    PrintStream out = System.out;

    @Override
    public void init(DiagnosticLog diagnosticLog) {
        this.dlog = diagnosticLog;
    }


    @Override
    public void codeGenerated(Path binaryPath) {
        String filePath = binaryPath.toAbsolutePath().toString();
        String targetFilePath = new File(filePath).getParentFile().getAbsolutePath() + File.separator + "target";
        try {
            byte[] bFile = Files.readAllBytes(Paths.get(filePath));
            ProgramFileReader reader = new ProgramFileReader();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bFile);
            ProgramFile programFile = reader.readProgram(byteArrayInputStream);
            PackageInfo packageInfos[] = programFile.getPackageInfoEntries();

            for (PackageInfo packageInfo : packageInfos) {
                ServiceInfo serviceInfos[] = packageInfo.getServiceInfoEntries();
                for (ServiceInfo serviceInfo : serviceInfos) {
                    AnnAttachmentInfo greetingAnnotation = serviceInfo.getAnnotationAttachmentInfo
                            ("ballerina.hello", "greeting");
                    String salutation = greetingAnnotation.getAttributeValue("salutation") != null ?
                            greetingAnnotation.getAttributeValue("salutation").getStringValue() : null;
                    if (salutation != null) {
                        targetFilePath += File.separator + serviceInfo.getName() + ".txt";
                        writeToFile(salutation, targetFilePath);
                    }
                }
            }
        } catch (IOException e) {
            out.println("error occurred while reading balx file" + e.getMessage());
        }
    }

    /**
     * Write content to a File. Create the required directories if they don't not exists.
     *
     * @param context        context of the file
     * @param targetFilePath target file path
     * @throws IOException If an error occurs when writing to a file
     */
    private void writeToFile(String context, String targetFilePath) throws IOException {
        File newFile = new File(targetFilePath);
        if (newFile.exists() && newFile.delete()) {
            Files.write(Paths.get(targetFilePath), context.getBytes(StandardCharsets.UTF_8));
            return;
        }
        if (newFile.getParentFile().mkdirs()) {
            Files.write(Paths.get(targetFilePath), context.getBytes(StandardCharsets.UTF_8));
            return;
        }
        Files.write(Paths.get(targetFilePath), context.getBytes(StandardCharsets.UTF_8));
    }
}

