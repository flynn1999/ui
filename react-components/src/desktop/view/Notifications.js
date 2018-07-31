/**
 * @author sriram
 */
import React, { Component } from "react";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import DEHyperlink from "../../../src/util/hyperlink/DEHyperLink";
import styles from "../style";
import Divider from "@material-ui/core/Divider";
import withI18N, { getMessage } from "../../util/I18NWrapper";
import intlData from "../messages";
import ids from "../ids";
import build from "../../util/DebugIDUtil";
import { withStyles } from "@material-ui/core/styles";
import RefreshIcon from "@material-ui/icons/Refresh";
import Button from "@material-ui/core/Button";
import CircularProgress from "@material-ui/core/CircularProgress";
import tourStrings from "../NewUserTourStrings";
import notificationImg from "../../resources/images/notification.png";


function ErrorComponent(props) {
    return (
        <div style={{outline: 'none'}}>
            <div className={props.classes.notificationError}>
                {getMessage("notificationError")}
            </div>
            <div id={build(ids.DESKTOP,ids.RETRY_BTN)}>
                <Button variant="fab"
                        mini="true"
                        className={props.classes.errorRetryButton}
                        onClick={props.onClick}>
                    <RefreshIcon />
                </Button>
            </div>
        </div>
    )
}

function NotificationFooter(props) {
    return (
        <MenuItem>
            {(props.unSeenCount > 10) ?
                <div>
                     <span id={build(ids.DESKTOP, ids.NEW_NOTIFICATIONS)}>
                         <DEHyperlink
                             onClick={props.viewNewNotification}
                             text={getMessage("newNotifications", {values: {count: props.unSeenCount}})}/>
                     </span>
                    <span style={{margin: '20px'}}> </span>
                    <span id={build(ids.DESKTOP, ids.MARK_ALL_SEEN)}>
                            <DEHyperlink
                                onClick={props.markAllAsSeen}
                                text={getMessage("markAllRead")}/>
                    </span>
                </div>
                :
                <span id={build(ids.DESKTOP, ids.SEE_ALL_NOTIFICATIONS)}>
                    <DEHyperlink
                        onClick={props.viewAllNotification}
                        text={getMessage("viewAllNotifi")}/>
                </span>
            }
        </MenuItem>
    )
}

class Notifications extends Component {
    constructor(props) {
        super(props);
        this.state = {
            anchorEl: null,
        };
        this.handleNotificationsClick = this.handleNotificationsClick.bind(this);
        this.onMenuItemSelect = this.onMenuItemSelect.bind(this);
        this.getNotification = this.getNotification.bind(this);
        this.getInteractiveAnalysisUrl = this.getInteractiveAnalysisUrl.bind(this);
        this.notificationBtn = React.createRef();
    }

    handleNotificationsClick() {
        this.setState({anchorEl: document.getElementById(this.props.anchor)});
    }

    handleClose = () => {
        this.setState({anchorEl: null});
    };

    onMenuItemSelect(event) {
        this.props.notificationClicked(event.currentTarget.id);
    }

    componentDidMount() {
        this.notificationBtn.current.setAttribute("data-intro", tourStrings.introNotifications);
        this.notificationBtn.current.setAttribute("data-position", "left");
        this.notificationBtn.current.setAttribute("data-step", "4");
    }

    getNotification(notification) {
        if (notification.seen) {
            return (
                <span key={notification.message.id} style={{outline: 'none'}}>
                    <MenuItem id={notification.message.id}
                              onClick={this.onMenuItemSelect}
                              style={{fontSize: 10,}}>
                        {notification.message.text}
                        {notification.payload.access_url &&
                            this.getInteractiveAnalysisUrl(notification)
                        }

                    </MenuItem>
                    <Divider/>
                </span>
            );
        } else {
            return (
                <span key={notification.message.id} style={{outline: 'none'}}>
                    <MenuItem id={notification.message.id}
                              onClick={this.onMenuItemSelect}
                              style={{backgroundColor: '#99d9ea', fontSize: 10, borderBottom: 1}}>
                        {notification.message.text}
                        {notification.payload.access_url &&
                             this.getInteractiveAnalysisUrl(notification)
                        }
                    </MenuItem>
                    <Divider/>
                </span>
            );
        }

    }

    getInteractiveAnalysisUrl(notification) {
        return (
            <span>
                  {getMessage("interactiveAnalysisUrl")}
                <a href={notification.payload.access_url}
                   target="_blank">
                    {getMessage("urlPrompt")}
                </a>
            </span>
        );
    }

    render() {
        const {
            anchorEl,
        } = this.state;

        const {notifications, unSeenCount, classes, notificationLoading, error} = this.props;
        const messages = (notifications && notifications.messages && notifications.messages.length > 0) ? notifications.messages : [];


        return (
            <span>
                    <img className={classes.menuIcon}
                         src={notificationImg}
                         alt="Notifications"
                         onClick={this.handleNotificationsClick}
                         ref={this.notificationBtn}></img>
                {unSeenCount !== "0" &&
                <span id='notifyCount'
                      className={classes.unSeenCount}>
                        {unSeenCount}
                    </span>
                }
                <Menu id={build(ids.DESKTOP, ids.NOTIFICATIONS_MENU)}
                      anchorEl={anchorEl}
                      open={Boolean(anchorEl)}
                      onClose={this.handleClose}
                      style={{width: '100%'}}>
                        {notificationLoading ? (
                                <CircularProgress size={30}
                                                  className={classes.loadingStyle}
                                                  thickness={7}/>
                            )
                            : (error ? (
                                    <ErrorComponent classes={classes}
                                                    onClick={this.props.fetchNotifications}/>
                                ) : (
                                    (messages.length > 0) ?
                                        messages.map(n => {
                                            return (
                                                this.getNotification(n)
                                            )
                                        }).reverse() : (
                                            <MenuItem id={build(ids.DESKTOP, ids.EMPTY_NOTIFICATION)}
                                                      onClick={this.onMenuItemSelect}>
                                                <div className={classes.notificationError}>
                                                    {getMessage("noNotifications")}
                                                </div>
                                            </MenuItem>
                                        )))}
                    <NotificationFooter unSeenCount={unSeenCount}
                                        viewAllNotification={this.props.viewAllNotification}
                                        markAllAsSeen={this.props.markAllAsSeen}
                                        viewNewNotification={this.props.viewNewNotification}
                                        />
                    </Menu>
            </span>
        );
    }
}

export default withStyles(styles)(withI18N(Notifications, intlData));


