import React, {Component} from 'react';

import Checkbox from 'material-ui/Checkbox';
import MenuItem from 'material-ui/MenuItem';
import PropTypes from 'prop-types';
import RaisedButton from 'material-ui/RaisedButton';
import {SaveSearchButton} from '../../diskResource/search';
import SelectField from 'material-ui/SelectField';
import TagSearchField from '../../tags/tagSearch/TagSearchField';
import TextField from 'material-ui/TextField';
import injectSheet from 'react-jss';
import styles from './styles';
import ids from './ids';
import {getMessage} from '../../util/hasI18N';
import {Field, Fields, reduxForm} from 'redux-form';

class SearchFormBase extends Component {

    constructor(props) {
        super(props);

        this.handleSubmitForm = this.handleSubmitForm.bind(this);
        this.handleSaveSearch = this.handleSaveSearch.bind(this);

        //Tags
        this.onEditTagSelected = this.onEditTagSelected.bind(this);
        this.fetchTagSuggestions = this.fetchTagSuggestions.bind(this);
    }

    handleSaveSearch(values) {
        let initialValues = this.props.initialValues;
        let originalName = initialValues ? initialValues.label : null;
        this.props.presenter.onSaveSearch(values, originalName);
    }

    handleSubmitForm(values) {
        this.props.presenter.onSearchBtnClicked(values);
    }

    onEditTagSelected(tag) {
        this.props.presenter.onEditTagSelected(tag);
    }

    fetchTagSuggestions(search) {
        this.props.presenter.fetchTagSuggestions(search);
    }

    render() {
        let classes = this.props.classes;
        let messages = this.props.i18nData;
        let dateIntervals = this.props.dateIntervals;
        let dateIntervalChildren = dateIntervals.map(function (item, index) {
            return <MenuItem key={index} value={item} primaryText={item.label}/>
        });
        let suggestedTags = this.props.suggestedTags;
        let originalName = this.props.initialValues ? this.props.initialValues : null;

        let sizesList = messages.fileSizes;
        let sizesListChildren = sizesList.map(function (item, index) {
            return <MenuItem key={index} value={item} primaryText={item}/>
        });

        // From redux
        let { handleSubmit, array, initialized, pristine } = this.props;

        return (
            <form id={ids.form}>
                <table className={classes.form}>
                    <tbody>
                    <tr>
                        <td>
                            <Field name='fileQuery'
                                   id={ids.nameHas}
                                   label={getMessage('nameHas')}
                                   component={renderTextField}/>
                        </td>
                        <td>
                            <Field name='createdWithin'
                                   id={ids.createdWithin}
                                   label={getMessage('createdWithin')}
                                   component={renderDropDown}>
                                {dateIntervalChildren}
                            </Field>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <Field name='negatedFileQuery'
                                   id={ids.nameHasNot}
                                   label={getMessage('nameHasNot')}
                                   component={renderTextField}/>
                        </td>
                        <td>
                            <Field name='modifiedWithin'
                                   id={ids.modifiedWithin}
                                   label={getMessage('modifiedWithin')}
                                   component={renderDropDown}>
                                {dateIntervalChildren}
                            </Field>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <Field name='metadataAttributeQuery'
                                   id={ids.metadataAttributeHas}
                                   label={getMessage('metadataAttributeHas')}
                                   component={renderTextField}/>
                        </td>
                        <td>
                            <Field name='ownedBy'
                                   id={ids.ownedBy}
                                   label={getMessage('ownedBy')}
                                   hintText={getMessage('enterCyVerseUserName')}
                                   component={renderTextField}/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <Field name='metadataValueQuery'
                                   id={ids.metadataValueHas}
                                   label={getMessage('metadataValueHas')}
                                   component={renderTextField}/>
                        </td>
                        <td>
                            <Field name='sharedWith'
                                   id={ids.sharedWith}
                                   label={getMessage('sharedWith')}
                                   hintText={getMessage('enterCyVerseUserName')}
                                   component={renderTextField}/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <table>
                                <tbody>
                                <tr>
                                    <td style={{'width': '100%'}}>
                                        <Field name='fileSizeGreater'
                                               type='number'
                                               min='0'
                                               id={ids.fileSizeGreater}
                                               label={getMessage('fileSizeGreater')}
                                               floatingLabelShrinkStyle={{width: '150%'}}
                                               component={renderTextField}/>
                                    </td>
                                    <td>
                                        <Field name='fileSizeGreaterUnit'
                                               id={ids.fileSizeGreaterUnit}
                                               label=' '
                                               component={renderDropDown}>
                                            {sizesListChildren}
                                        </Field>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                        <td>
                            <table>
                                <tbody>
                                <tr>
                                    <td style={{'width': '100%'}}>
                                        <Field name='fileSizeLessThan'
                                               type='number'
                                               min='0'
                                               id={ids.fileSizeLessThan}
                                               label={getMessage('fileSizeLessThan')}
                                               floatingLabelShrinkStyle={{width: '150%'}}
                                               component={renderTextField}/>
                                    </td>
                                    <td>
                                        <Field name='fileSizeLessThanUnit'
                                               id={ids.fileSizeLessThanUnit}
                                               label=' '
                                               component={renderDropDown}>
                                            {sizesListChildren}
                                        </Field>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <Fields names={['taggedWith', 'tagQuery']}
                                    parentId={ids.form}
                                    label={getMessage('taggedWith')}
                                    onEditTagSelected={this.onEditTagSelected}
                                    fetchTagSuggestions={this.fetchTagSuggestions}
                                    suggestedTags={suggestedTags}
                                    array={array}
                                    component={renderTagSearchField}/>
                        </td>
                        <td>
                            <Field name='includeTrashItems'
                                   id={ids.includeTrash}
                                   label={getMessage('includeTrash')}
                                   style={{top: '20px'}}
                                   component={renderCheckBox}/>
                        </td>

                    </tr>

                    <tr>
                        <td>
                            <Field name='label'
                                   originalName={originalName}
                                   disabled={initialized ? false : pristine}
                                   id={ids.saveSearchBtn}
                                   handleSave={handleSubmit(this.handleSaveSearch)}
                                   component={renderSaveSearchBtn}/>
                        </td>
                        <td>
                            <div className={classes.searchButton}>
                                <RaisedButton id={ids.searchBtn}
                                              disabled={initialized ? false : pristine}
                                              label={getMessage('searchBtn')}
                                              onClick={handleSubmit(this.handleSubmitForm)}/>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        )
    }
}

SearchFormBase.propTypes = {
    presenter: PropTypes.shape({
        onSearchBtnClicked: PropTypes.func.isRequired,
        onEditTagSelected: PropTypes.func.isRequired,
        onSaveSearch: PropTypes.func.isRequired,
        fetchTagSuggestions: PropTypes.func.isRequired
    }),
    dateIntervals: PropTypes.arrayOf(
        PropTypes.shape({
            label: PropTypes.string.isRequired,
            from: PropTypes.number,
            to: PropTypes.number
        })
    ),
    suggestedTags: PropTypes.arrayOf(PropTypes.shape({
        value: PropTypes.string.isRequired,
        description: PropTypes.string
    })),
    id: PropTypes.string.isRequired,
    initialValues: PropTypes.shape({
            label: PropTypes.string,
            path: PropTypes.string,
            fileQuery: PropTypes.string,
            negatedFileQuery: PropTypes.string,
            createdWithin: PropTypes.number,
            modifiedWithin: PropTypes.number,
            metadataAttributeQuery: PropTypes.string,
            metadataValueQuery: PropTypes.string,
            ownedBy: PropTypes.string,
            sharedWith: PropTypes.string,
            fileSizeGreater: PropTypes.string,
            fileSizeGreaterUnit: PropTypes.string,
            fileSizeLessThan: PropTypes.string,
            fileSizeLessThanUnit: PropTypes.string,
            tagQuery: PropTypes.array,
            includeTrashItems: PropTypes.bool
        }
    )
};

function renderSaveSearchBtn(props) {
    let {
        input,
        disabled,
        ...custom
    } = props;
    return (
        <SaveSearchButton disabled={disabled}
                          {...input}
                          {...custom}
        />
    )
}

function renderTagSearchField(props) {
    let {
        label,
        taggedWith,
        tagQuery,
        array,
        ...custom
    } = props;
    return (
        <TagSearchField
            label={label}
            taggedWith={taggedWith.input.value}
            tags={tagQuery.input.value ? tagQuery.input.value : []}
            onTagValueChange={(value) => taggedWith.input.onChange(value)}
            onTagSelected={(value) => onTagSelected(value, array, taggedWith)}
            onDeleteTagSelected={(index) => array.remove('tagQuery', index)}
            {...custom}/>
    )
}

function onTagSelected(value, array, taggedWith) {
    array.insert('tagQuery', 0, value);
    taggedWith.input.onChange('');
}

function renderTextField(props) {
    let {
        input,
        label,
        meta: {touched, error},
        ...custom
    } = props;
    return (
        <TextField
            floatingLabelText={label}
            errorText={touched && error}
            fullWidth={true}
            {...input}
            {...custom}
        />
    )
}

function renderDropDown(props) {
    let {
        input,
        label,
        children,
        ...custom,
    } = props;

    return (
        <SelectField floatingLabelText={label}
                     fullWidth={true}
                     {...input}
                     onChange={(event, index, value) => input.onChange(value)}
                     children={children}
                     {...custom}
        />
    )
}

function renderCheckBox(props) {
    let {
        input,
        label,
        ...custom
    } = props;
    return (
        <Checkbox label={label}
                  checked={input.value ? true : false}
                  onCheck={input.onChange}
                  {...custom}
        />
    )
}

SearchFormBase = injectSheet(styles)(SearchFormBase);
export default reduxForm(
    {
        form: 'searchForm',
        enableReinitialize: true
    }
)(SearchFormBase);
