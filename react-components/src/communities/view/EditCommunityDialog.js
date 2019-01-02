import build from "../../util/DebugIDUtil";
import ConfirmCloseDialog from "../../util/ConfirmCloseDialog";
import EditCommunity from "./EditCommunity";
import { getMessage } from "../../util/I18NWrapper";
import ids from "../ids";
import messages from "../messages";
import withI18N from "../../util/I18NWrapper";

import Button from "@material-ui/core/Button/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import PropTypes from "prop-types";
import React, { Component, Fragment } from "react";

/**
 * @author aramsey
 * A simple button that allows users to create communities
 */
class EditCommunityDialog extends Component {
    constructor(props) {
        super(props);

        this.state = {
            saveCommunity: false,
            deleteCommunity: false,
            leaveCommunity: false,
            joinCommunity: false,
        };

        [
            'onDialogBtnClicked',
            'closeDialog',
            'handleDialogConfirmed',
            'handleCloseEditDialog',
        ].forEach((fn) => this[fn] = this[fn].bind(this));
    }

    onDialogBtnClicked(dialogName) {
        this.setState({[dialogName]: true})
    }

    closeDialog(dialogName) {
        this.setState({[dialogName]: false})
    }

    handleDialogConfirmed(dialogName) {
        const {community} = this.props;

        this.props.presenter[[dialogName]](community, () => this.closeDialog([dialogName]));
    }

    handleCloseEditDialog() {
        this.setState({
            saveCommunity: false,
        });

        this.props.onClose();
    }

    render() {
        const {
            community,
            isCommunityAdmin,
            isMember,
            onClose,
            open,
            collaboratorsUtil,
            presenter,
        } = this.props;

        const {saveCommunity} = this.state;

        const communityName = community !== null ? collaboratorsUtil.getSubjectDisplayName(community) : null;

        return (
            <Fragment>
                <Dialog open={open}
                        fullWidth={true}
                        maxWidth='lg'
                        onClose={onClose}
                        id={ids.EDIT_DLG}>
                    <DialogTitle>
                        <DialogHeader community={community}
                                      communityName={communityName}
                                      isCommunityAdmin={isCommunityAdmin}/>
                    </DialogTitle>
                    <DialogContent>
                        <EditCommunity parentId={ids.EDIT_DLG}
                                       community={community}
                                       isCommunityAdmin={isCommunityAdmin}
                                       collaboratorsUtil={collaboratorsUtil}
                                       presenter={presenter}
                                       saveCommunity={saveCommunity}
                                       onCommunitySaved={this.handleCloseEditDialog}/>
                    </DialogContent>
                    <DialogActions>
                        {community && isCommunityAdmin &&
                        <Button variant="raised"
                                id={build(ids.EDIT_DLG, ids.BUTTONS.DELETE_COMMUNITY)}
                                onClick={() => this.onDialogBtnClicked('deleteCommunity')}>
                            {getMessage('deleteCommunity')}
                        </Button>
                        }
                        {community && isMember &&
                        <Button variant="raised"
                                id={build(ids.EDIT_DLG, ids.BUTTONS.LEAVE_COMMUNITY)}
                                onClick={() => this.onDialogBtnClicked('leaveCommunity')}>
                            {getMessage('leaveCommunity')}
                        </Button>
                        }
                        {!isMember &&
                        <Button variant="raised"
                                id={build(ids.EDIT_DLG, ids.BUTTONS.JOIN_COMMUNITY)}
                                onClick={() => this.onDialogBtnClicked('joinCommunity')}>
                            {getMessage('joinCommunity')}
                        </Button>}
                        {isCommunityAdmin &&
                        <Button variant="raised"
                                id={build(ids.EDIT_DLG, ids.BUTTONS.OK)}
                                onClick={() => this.onDialogBtnClicked('saveCommunity')}>
                            {getMessage('ok')}
                        </Button>
                        }
                        <Button variant="raised"
                                id={build(ids.EDIT_DLG, ids.BUTTONS.CANCEL)}
                                onClick={this.handleCloseEditDialog}>
                            {getMessage('cancel')}
                        </Button>
                    </DialogActions>
                </Dialog>

                <ConfirmCloseDialog open={this.state.deleteCommunity}
                                    parentId={ids.CONFIRM_DELETE_DLG}
                                    onConfirm={() => this.handleDialogConfirmed('deleteCommunity')}
                                    confirmLabel={getMessage('yes')}
                                    onCancel={() => this.closeDialog('deleteCommunity')}
                                    cancelLabel={getMessage('cancel')}
                                    onClose={() => this.closeDialog('deleteCommunity')}
                                    closeLabel={getMessage('no')}
                                    title={getMessage('confirmDeleteCommunityTitle', {values: {name: communityName}})}
                                    dialogContent={getMessage('confirmDeleteCommunity')}/>

                <ConfirmCloseDialog open={this.state.joinCommunity}
                                    parentId={ids.CONFIRM_JOIN_DLG}
                                    onConfirm={() => this.handleDialogConfirmed('joinCommunity')}
                                    confirmLabel={getMessage('yes')}
                                    onCancel={() => this.closeDialog('joinCommunity')}
                                    cancelLabel={getMessage('cancel')}
                                    onClose={() => this.closeDialog('joinCommunity')}
                                    closeLabel={getMessage('no')}
                                    title={getMessage('confirmJoinCommunityTitle', {values: {name: communityName}})}
                                    dialogContent={getMessage('confirmJoinCommunity')}/>

                <ConfirmCloseDialog open={this.state.leaveCommunity}
                                    parentId={ids.CONFIRM_LEAVE_DLG}
                                    onConfirm={() => this.handleDialogConfirmed('leaveCommunity')}
                                    confirmLabel={getMessage('yes')}
                                    onCancel={() => this.closeDialog('leaveCommunity')}
                                    cancelLabel={getMessage('cancel')}
                                    onClose={() => this.closeDialog('leaveCommunity')}
                                    closeLabel={getMessage('no')}
                                    title={getMessage('confirmLeaveCommunityTitle', {values: {name: communityName}})}
                                    dialogContent={getMessage('confirmLeaveCommunity')}/>
            </Fragment>
        )
    }
}

function DialogHeader(props) {
    const {
        community,
        communityName,
        isCommunityAdmin,
    } = props;

    if (community === null) {
        return (
            getMessage('createCommunityTitle')
        )
    } else if (community && isCommunityAdmin) {
        return (
            getMessage('editCommunityTitle', {values: {name: communityName}})
        )
    } else {
        return (
            communityName
        )
    }
}

EditCommunityDialog.propTypes = {
    collaboratorsUtil: PropTypes.object.isRequired,
    onClose: PropTypes.func.isRequired,
    open: PropTypes.bool.isRequired,
    presenter: PropTypes.shape({
        deleteCommunity: PropTypes.func.isRequired,
        joinCommunity: PropTypes.func.isRequired,
        leaveCommunity: PropTypes.func.isRequired,
    }),
    community: PropTypes.object,
    isCommunityAdmin: PropTypes.bool.isRequired,
    isMember: PropTypes.bool.isRequired,
};

export default withI18N(EditCommunityDialog, messages);