package org.iplantc.de.apps.integration.client.view.propertyEditors;

import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.Ids;
import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.PropertyPanelIds;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToFileConverter;
import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.FileInfoType;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.services.AppBuilderMetadataServiceFacade;
import org.iplantc.de.commons.client.validators.CmdLineArgCharacterValidator;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorFieldFactory;
import org.iplantc.de.diskResource.client.views.widgets.FileSelectorField;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author jstroot
 */
public class FileInputPropertyEditor extends AbstractArgumentPropertyEditor {

    interface EditorDriver extends SimpleBeanEditorDriver<Argument, FileInputPropertyEditor> { }

    interface FileInputPropertyEditorUiBinder extends UiBinder<Widget, FileInputPropertyEditor> { }

    @UiField(provided = true) PropertyEditorAppearance appearance;
    @UiField @Path("name") TextField argumentOptionEditor;
    @UiField FieldLabel argumentOptionLabel;
    @UiField(provided = true) @Ignore ComboBox<FileInfoType> fileInfoTypeComboBox;
    @UiField TextField label;
    @UiField CheckBoxAdapter requiredEditor, omitIfBlank;
    @UiField @Path("description") TextField toolTipEditor;
    @UiField FieldLabel toolTipLabel, defaultValueLabel;
    @UiField @Path("fileParameters.implicit") CheckBoxAdapter isImplicit;
    @UiField(provided = true)
    ArgumentEditorConverter<File> defaultValueEditor;
    @UiField @Path("visible") CheckBoxAdapter doNotDisplay;

    private static FileInputPropertyEditorUiBinder uiBinder = GWT.create(FileInputPropertyEditorUiBinder.class);
    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @Inject
    public FileInputPropertyEditor(final DiskResourceSelectorFieldFactory fileSelectorFieldFactory,
                                   final PropertyEditorAppearance appearance,
                                   final AppBuilderMetadataServiceFacade appMetadataService,
                                   final IplantValidationConstants validationConstants) {
        this.appearance = appearance;
        FileSelectorField fileSelectorField = fileSelectorFieldFactory.defaultFileSelector();
        defaultValueEditor = new ArgumentEditorConverter<>(fileSelectorField, new SplittableToFileConverter());
        fileInfoTypeComboBox = createFileInfoTypeComboBox(appMetadataService);

        initWidget(uiBinder.createAndBindUi(this));

        argumentOptionEditor.addValidator(new CmdLineArgCharacterValidator(validationConstants.restrictedCmdLineChars()));
        defaultValueLabel.setHTML(appearance.createContextualHelpLabel(appearance.fileInputDefaultLabel()
                , appearance.fileInputDefaultText()));
        
        toolTipLabel.setHTML(appearance.createContextualHelpLabel(appearance.toolTipText(), appearance.toolTip()));
        argumentOptionLabel.setHTML(appearance.createContextualHelpLabel(appearance.argumentOption(), appearance.argumentOptionHelp()));
        doNotDisplay.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appearance.doNotDisplay()).toSafeHtml());

        requiredEditor.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appearance.isRequired()).toSafeHtml());
        omitIfBlank.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appearance.createContextualHelpLabelNoFloat(appearance.excludeWhenEmpty(), appearance.fileInputExcludeArgument()))
                                                 .toSafeHtml());
        isImplicit.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appearance.createContextualHelpLabelNoFloat(appearance.isImplicit(), appearance.fileInputIsImplicit())).toSafeHtml());
        editorDriver.initialize(this);
        editorDriver.accept(new InitializeTwoWayBinding(this));
        ensureDebugId(Ids.PROPERTY_EDITOR + Ids.FILE_INPUT);
    }

    @Override
    public void edit(Argument argument) {
        super.edit(argument);
        editorDriver.edit(argument);
    }

    @Override
    public com.google.gwt.editor.client.EditorDriver<Argument> getEditorDriver() {
        return editorDriver;
    }

    @Override
    @Ignore
    protected ComboBox<FileInfoType> getFileInfoTypeComboBox() {
        return fileInfoTypeComboBox;
    }

    @Override
    protected void initLabelOnlyEditMode(boolean isLabelOnlyEditMode) {
        fileInfoTypeComboBox.setEnabled(!isLabelOnlyEditMode);
        argumentOptionEditor.setEnabled(!isLabelOnlyEditMode);
        requiredEditor.setEnabled(!isLabelOnlyEditMode);
        omitIfBlank.setEnabled(!isLabelOnlyEditMode);

        if (isLabelOnlyEditMode) {
            fileInfoTypeComboBox.getValidators().clear();
            argumentOptionEditor.getValidators().clear();
            requiredEditor.getValidators().clear();
            omitIfBlank.getValidators().clear();
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        label.ensureDebugId(baseID + PropertyPanelIds.LABEL);
        argumentOptionEditor.ensureDebugId(baseID + PropertyPanelIds.ARGUMENT_OPTION);
        requiredEditor.ensureDebugId(baseID + PropertyPanelIds.REQUIRED);
        omitIfBlank.ensureDebugId(baseID + PropertyPanelIds.OMIT_IF_BLANK);
        toolTipEditor.ensureDebugId(baseID + PropertyPanelIds.TOOL_TIP);
        fileInfoTypeComboBox.ensureDebugId(baseID + PropertyPanelIds.FILE_INFO_TYPE);
    }

}
