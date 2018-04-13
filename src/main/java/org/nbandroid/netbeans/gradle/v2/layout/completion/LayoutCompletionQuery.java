/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.nbandroid.netbeans.gradle.v2.layout.completion;

import com.android.builder.model.AndroidProject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.xml.namespace.QName;
import org.nbandroid.netbeans.gradle.v2.layout.AndroidStyleable;
import org.nbandroid.netbeans.gradle.v2.layout.AndroidStyleableAttr;
import org.nbandroid.netbeans.gradle.v2.layout.AndroidStyleableNamespace;
import org.nbandroid.netbeans.gradle.v2.layout.AndroidStyleableStore;
import org.nbandroid.netbeans.gradle.v2.layout.AndroidStyleableType;
import org.nbandroid.netbeans.gradle.v2.layout.tools.ToolsNamespaceProvider;
import org.nbandroid.netbeans.gradle.v2.sdk.java.platform.AndroidJavaPlatform;
import org.nbandroid.netbeans.gradle.v2.sdk.java.platform.AndroidJavaPlatformProvider;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.gradle.project.NbGradleProject;
import org.netbeans.modules.xml.schema.completion.spi.CompletionContext;
import org.netbeans.modules.xml.schema.completion.util.CompletionContextImpl;
import org.netbeans.modules.xml.schema.completion.util.CompletionUtil;
import org.netbeans.modules.xml.text.syntax.XMLSyntaxSupport;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author arsi
 */
public class LayoutCompletionQuery extends AsyncCompletionQuery {

    private enum QueryType {
        ELEMENT,
        ATTRIBUTE,
        VALUE,
        UNKNOWN
    }

    private JTextComponent component;
    private final FileObject primaryFile;
    private QueryType queryType = QueryType.UNKNOWN;
    private CompletionContext.CompletionType currentMode = CompletionContext.CompletionType.COMPLETION_TYPE_UNKNOWN;
    private Map<String, AndroidStyleable> items = new HashMap<>();
    private String startChars = "";
    private boolean namespaceCompletion = false;
    private final List<AttrCompletionItem> styleableAttrs = new ArrayList<>();

    LayoutCompletionQuery(FileObject primaryFile, int queryType) {
        this.primaryFile = primaryFile;
    }

    @Override
    protected void prepareQuery(JTextComponent component) {
        this.component = component;
    }

    @Override
    protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
        resultSet.setWaitText("Loading android Styleables..");
        XMLSyntaxSupport support = (XMLSyntaxSupport) ((BaseDocument) doc).getSyntaxSupport();
        if (!support.noCompletion(component) && CompletionUtil.canProvideCompletion((BaseDocument) doc)) {
            Project owner = FileOwnerQuery.getOwner(primaryFile);
            if (owner instanceof NbGradleProject) {
                AndroidProject androidProject = ((NbGradleProject) owner).getLookup().lookup(AndroidProject.class);
                if (androidProject != null) {
                    String next = androidProject.getBootClasspath().iterator().next();
                    AndroidJavaPlatform findPlatform = AndroidJavaPlatformProvider.findPlatform(next, androidProject.getCompileTarget());
                    if (findPlatform != null) {
                        CompletionContextImpl context = new CompletionContextImpl(primaryFile, support, caretOffset);
                        if (context.initContext()) {
                            CompletionContext.CompletionType completionType = context.getCompletionType();
                            switch (completionType) {
                                case COMPLETION_TYPE_UNKNOWN:
                                    //handle bug in CompletionContextImpl
                                    //at end of attr name it returns COMPLETION_TYPE_UNKNOWN
                                    if (caretOffset > 10) {
                                        context = new CompletionContextImpl(primaryFile, support, caretOffset - 1);
                                        if (context.initContext()) {
                                            if (context.getCompletionType() == CompletionContext.CompletionType.COMPLETION_TYPE_ATTRIBUTE) {
                                                makeAttribute(findPlatform, context, primaryFile, androidProject, resultSet, doc, caretOffset);
                                            }
                                        }
                                    }
                                    break;
                                case COMPLETION_TYPE_ATTRIBUTE:
                                    queryType = QueryType.ATTRIBUTE;
                                    makeAttribute(findPlatform, context, primaryFile, androidProject, resultSet, doc, caretOffset);
                                    break;
                                case COMPLETION_TYPE_ATTRIBUTE_VALUE:
                                    queryType = QueryType.VALUE;
                                    makeAttributeValue(findPlatform, context, primaryFile, androidProject, resultSet, doc, caretOffset);
                                    break;
                                case COMPLETION_TYPE_ELEMENT:
                                    queryType = QueryType.ELEMENT;
                                    makeElement(findPlatform, context, primaryFile, androidProject, resultSet, doc, caretOffset);
                                    break;
                                case COMPLETION_TYPE_ELEMENT_VALUE:
                                    break;
                                case COMPLETION_TYPE_ENTITY:
                                    break;
                                case COMPLETION_TYPE_NOTATION:
                                    break;
                                case COMPLETION_TYPE_DTD:
                                    break;

                            }

                        }
                    }
                }
            }
        }
        resultSet.finish();
    }
    private String typedCharsFilter = "";

    @Override
    protected boolean canFilter(JTextComponent component) {
        switch (queryType) {
            case ELEMENT:
                return canFilterElement(component);
            case ATTRIBUTE:
                return canFilterAttribute(component);
            case VALUE:
                return canFilterValue(component);
            default:
                return false;

        }
    }

    private boolean canFilterElement(JTextComponent component) {
        try {
            typedCharsFilter = Utilities.getIdentifierBefore((BaseDocument) component.getDocument(), component.getCaretPosition());
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (typedCharsFilter == null) {
            typedCharsFilter = "";
        }
        typedCharsFilter = typedCharsFilter.toLowerCase();
        return true;
    }

    private boolean canFilterAttribute(JTextComponent component) {
        try {
            typedCharsFilter = Utilities.getIdentifierBefore((BaseDocument) component.getDocument(), component.getCaretPosition());
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (typedCharsFilter == null) {
            typedCharsFilter = "";
        }
        typedCharsFilter = typedCharsFilter.toLowerCase();
        return true;
    }

    private boolean canFilterValue(JTextComponent component) {
        return false;
    }

    @Override
    protected void filter(CompletionResultSet resultSet) {
        switch (queryType) {
            case ELEMENT:
                filterElement(resultSet);
            case ATTRIBUTE:
                filterAttribute(resultSet);
                break;
            case VALUE:
                filterValue(resultSet);
                break;
            default:
                throw new AssertionError(queryType.name());

        }
        resultSet.finish();
    }

    private void filterElement(CompletionResultSet resultSet) {
        resultSet.addAllItems(items.values().stream().filter(c -> c.getLowerCaseFullClassName().startsWith(typedCharsFilter) || c.getLowerCaseName().startsWith(typedCharsFilter) || c.getUpperCaseLetters().equals(typedCharsFilter)).collect(Collectors.toList()));
    }

    private void filterAttribute(CompletionResultSet resultSet) {
        resultSet.addAllItems(styleableAttrs.stream().filter(c -> c.getLowerCasecompletionText().startsWith(typedCharsFilter) || c.getLowerCaseSimpleCompletionText().startsWith(typedCharsFilter)).collect(Collectors.toList()));
    }

    private void filterValue(CompletionResultSet resultSet) {

    }

    private void makeAttribute(AndroidJavaPlatform findPlatform, CompletionContextImpl context, FileObject primaryFile, AndroidProject androidProject, CompletionResultSet resultSet, Document doc, int caretOffset) {
        HashMap<String, String> declaredNamespaces = context.getDeclaredNamespaces();
        String typedChars = null;
        //when is cursor at end of text context returns null
        try {
            typedChars = Utilities.getIdentifierBefore((BaseDocument) doc, caretOffset);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        List<QName> pathFromRoot = context.getPathFromRoot();
        String attributeRoot = null;
        if (!pathFromRoot.isEmpty()) {
            QName qname = pathFromRoot.get(pathFromRoot.size() - 1);
            attributeRoot = qname.getLocalPart();
            AndroidStyleableNamespace platformWidgetNamespaces = findPlatform.getPlatformWidgetNamespaces();
        }
        if (typedChars == null) {
            typedChars = "";
        }
        if (attributeRoot == null || "".equals(attributeRoot)) {
            return;
        }
        final String typed = typedChars.toLowerCase();
        Map<String, AndroidStyleableNamespace> namespacesIn = AndroidStyleableStore.findNamespaces(primaryFile);
        AndroidStyleable styleable = findStyleable(attributeRoot, namespacesIn);
        if (styleable == null) {
            return;
        }
        styleableAttrs.addAll(styleable.getAllAttrs(declaredNamespaces));
        AndroidStyleable parentStyleable = null;
        if (pathFromRoot.size() > 1) {
            for (int i = 0; i < pathFromRoot.size() - 1; i++) {
                QName qname = pathFromRoot.get(i);
                attributeRoot = qname.getLocalPart();
                parentStyleable = findStyleable(attributeRoot, namespacesIn);
                if (parentStyleable != null && parentStyleable.getAndroidStyleableType() == AndroidStyleableType.Layout) {
                    List<AndroidStyleable> findAllLayoutParams = parentStyleable.findAllLayoutParams();
                    for (AndroidStyleable superS : findAllLayoutParams) {
                        List<AttrCompletionItem> allAttrs = superS.getAllAttrs(declaredNamespaces);
                        for (AttrCompletionItem allAttr : allAttrs) {
                            if (!styleableAttrs.contains(allAttr)) {
                                styleableAttrs.add(allAttr);
                            }
                        }
                    }
                }
            }

        } else {
            //#101 Root element styleable android:layout_width not found
            AndroidStyleable superStyleable = styleable.getSuperStyleable();
            if (superStyleable != null) {
                List<AndroidStyleable> findAllLayoutParams = superStyleable.findAllLayoutParams();
                for (AndroidStyleable superS : findAllLayoutParams) {
                    List<AttrCompletionItem> allAttrs = superS.getAllAttrs(declaredNamespaces);
                    for (AttrCompletionItem allAttr : allAttrs) {
                        if (!styleableAttrs.contains(allAttr)) {
                            styleableAttrs.add(allAttr);
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, String> entry : declaredNamespaces.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (AndroidStyleableStore.TOOLS_NAMESPACE.equalsIgnoreCase(value)) {
                List<AndroidStyleableAttr> allToolsAttrs = ToolsNamespaceProvider.getAllToolsAttrs();
                for (AndroidStyleableAttr allToolsAttr : allToolsAttrs) {
                    styleableAttrs.add(new AttrCompletionItem(null, allToolsAttr, key));
                }
            }
        }
        if ("".equals(typed)) {
            resultSet.addAllItems(styleableAttrs);
        } else {
            resultSet.addAllItems(styleableAttrs.stream().filter(c -> c.getLowerCasecompletionText().startsWith(typed) || c.getLowerCaseSimpleCompletionText().startsWith(typed)).collect(Collectors.toList()));

        }
        System.out.println("org.nbandroid.netbeans.gradle.v2.layout.completion.LayoutCompletionQuery.makeAttribute()");
    }

    public AndroidStyleable findStyleable(String attributeRoot, Map<String, AndroidStyleableNamespace> namespacesIn) {
        AndroidStyleable styleable = null;
        if (!attributeRoot.contains(".")) {
            //android
            for (Map.Entry<String, AndroidStyleableNamespace> entry : namespacesIn.entrySet()) {
                AndroidStyleableNamespace androidStyleableNamespace = entry.getValue();
                styleable = androidStyleableNamespace.getLayoutsSimpleNames().get(attributeRoot);
                if (styleable != null) {
                    break;
                }
                styleable = androidStyleableNamespace.getWitgetsSimpleNames().get(attributeRoot);
                if (styleable != null) {
                    break;
                }

            }
            return styleable;
        } else {
            for (Map.Entry<String, AndroidStyleableNamespace> entry : namespacesIn.entrySet()) {
                AndroidStyleableNamespace androidStyleableNamespace = entry.getValue();
                styleable = androidStyleableNamespace.getLayouts().get(attributeRoot);
                if (styleable != null) {
                    break;
                }
                styleable = androidStyleableNamespace.getWitgets().get(attributeRoot);
                if (styleable != null) {
                    break;
                }
            }
            return styleable;
        }
    }

    private void makeAttributeValue(AndroidJavaPlatform findPlatform, CompletionContextImpl context, FileObject primaryFile, AndroidProject androidProject, CompletionResultSet resultSet, Document doc, int caretOffset) {
        HashMap<String, String> declaredNamespaces = context.getDeclaredNamespaces();
        String attribute = context.getAttribute();
        String typedChars = context.getTypedChars();
        String attributeRoot = null;
        List<QName> pathFromRoot = context.getPathFromRoot();
        if (!pathFromRoot.isEmpty()) {
            QName qname = pathFromRoot.get(pathFromRoot.size() - 1);
            attributeRoot = qname.getLocalPart();
            AndroidStyleableNamespace platformWidgetNamespaces = findPlatform.getPlatformWidgetNamespaces();
        }
        System.out.println("org.nbandroid.netbeans.gradle.v2.layout.completion.LayoutCompletionQuery.makeAttributeValue()");
    }

    private void makeElement(AndroidJavaPlatform findPlatform, CompletionContextImpl context, FileObject primaryFile, AndroidProject androidProject, CompletionResultSet resultSet, Document doc, int caretOffset) {
        HashMap<String, String> declaredNamespaces = context.getDeclaredNamespaces();
        currentMode = CompletionContext.CompletionType.COMPLETION_TYPE_ELEMENT;
        String typedChars = context.getTypedChars();
        if (typedChars == null) {
            //when is cursor at end of text context returns null
            try {
                typedChars = Utilities.getIdentifierBefore((BaseDocument) doc, caretOffset);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        if (typedChars == null) {
            //cursor is after <
            typedChars = "";
        }
        final String typed = typedChars.toLowerCase();
        Map<String, AndroidStyleableNamespace> namespacesIn = AndroidStyleableStore.findNamespaces(primaryFile);
        Map<String, AndroidStyleableNamespace> namespaces = new HashMap<>();
        for (Map.Entry<String, String> entry : declaredNamespaces.entrySet()) {
            String name = entry.getKey();
            String nameSpace = entry.getValue();
            AndroidStyleableNamespace tmp = namespacesIn.get(nameSpace);
            if (tmp != null) {
                namespaces.put(name, tmp);
                items.putAll(tmp.getLayouts());
                items.putAll(tmp.getWitgets());
            }
        }
        if (namespaces.isEmpty()) {
            return;
        }
        startChars = typed;
        if ("".equals(typed)) {
            resultSet.addAllItems(items.values());
        } else {
            resultSet.addAllItems(items.values().stream().filter(c -> c.getLowerCaseFullClassName().startsWith(typed) || c.getLowerCaseName().startsWith(typed) || c.getUpperCaseLetters().equals(typed)).collect(Collectors.toList()));
        }
    }

}
