import * as React from 'react';
import { Route, RouteComponentProps, withRouter } from 'react-router-dom';
import { Container, Menu, MenuItem, MenuItemProps, MenuMenu } from 'semantic-ui-react';

import ClientPage from './client/ClientPage';
import CutoffPage from './cutoff/CutoffPage';
import GroupPage from './group/GroupPage';
import GroupTypePage from './grouptype/GroupTypePage';
import LoanPage from './loan/LoanPage';
import LoanTypePage from './loantype/LoanTypePage';
import SessionPage from './session/SessionPage';
import UserPage from './user/UserPage';
import UserProfilePage from './userprofile/UserProfilePage';


class Main extends React.Component<RouteComponentProps, any> {

    public render() {
        return (
            <Container fluid={true} style={{ height: "100vh" }}>
                <Menu inverted={true} style={{ borderRadius: 0 }} borderless={true} fixed="top">
                    <MenuItem icon="home" onClick={this.onDropdownClick} />
                    <MenuItem content="LoanMan" style={{ fontWeight: "bold" }} />
                    <MenuMenu position="right">
                        <MenuItem content="username" />
                        <MenuItem icon="cog" onClick={this.onDropdownClick} />
                    </MenuMenu>
                </Menu>
                <div style={{ height: "100vh", overflow: "hidden", paddingTop: 50}}>
                    <div style={{ minWidth: 220, float: "left", paddingLeft: 10 }}>
                        <Menu vertical={true} fluid={true}>
                            <MenuItem content="Cutoffs" targetpath="/cutoff" onClick={this.onNavigate} active={this.isCurrentPath("/cutoff")} />
                            <MenuItem content="Clients" targetpath="/client" onClick={this.onNavigate} active={this.isCurrentPath("/client")} />
                            <MenuItem content="Loans" targetpath="/loan" onClick={this.onNavigate} active={this.isCurrentPath("/loan")} />
                            <MenuItem content="Loan Types" targetpath="/loantype" onClick={this.onNavigate} active={this.isCurrentPath("/loantype")} />
                            <MenuItem content="Groups" targetpath="/group" onClick={this.onNavigate} active={this.isCurrentPath("/cutoff")} />
                            <MenuItem content="Group Types" targetpath="/grouptype" onClick={this.onNavigate} active={this.isCurrentPath("/grouptype")} />
                            <MenuItem content="Users" targetpath="/user" onClick={this.onNavigate} active={this.isCurrentPath("/user")} />
                            <MenuItem content="User Profiles" targetpath="/userprofile" onClick={this.onNavigate} active={this.isCurrentPath("/userprofile")} />
                            <MenuItem content="Sessions" targetpath="/session" onClick={this.onNavigate} active={this.isCurrentPath("/session")} />
                            <MenuItem content="Reports" targetpath="/report" onClick={this.onNavigate} active={this.isCurrentPath("/report")} />
                        </Menu>
                    </div>
                    <div style={{ marginLeft: 230, paddingLeft: 4, paddingRight: 10 }}>
                        <Route path="/loanman/cutoff" component={CutoffPage} />
                        <Route path="/loanman/client" component={ClientPage} />
                        <Route path="/loanman/loan" component={LoanPage} />
                        <Route path="/loanman/loantype" component={LoanTypePage} />
                        <Route path="/loanman/group" component={GroupPage} />
                        <Route path="/loanman/grouptype" component={GroupTypePage} />
                        <Route path="/loanman/user" component={UserPage} />
                        <Route path="/loanman/userprofile" component={UserProfilePage} />
                        <Route path="/loanman/session" component={SessionPage} />
                        <Route path="/loanman/report" />
                    </div>
                </div>
            </Container>
        );
    }

    private onDropdownClick = () => {
        alert("Whoa");
    }

    private isCurrentPath = (path: string) => {
        let location = this.props.location.pathname;
        path = "/loanman" + path;
        return location === path;
    }

    private onNavigate = (event: React.MouseEvent, data: MenuItemProps) => {
        let targetpath = data.targetpath as string;
        this.props.history.push("/loanman" + targetpath);
    }

}

export default withRouter(Main);