import * as React from 'react';
import { Container, Menu, MenuItem, MenuMenu } from 'semantic-ui-react';

import CutoffPage from './cutoff/CutoffPage';
import GroupForm from './group/GroupForm';

export default class Main extends React.Component {

    private loanTypeFormRef = React.createRef<GroupForm>();

    public render() {
        return (
            <Container fluid={true}>
                <Menu inverted={true} style={{ borderRadius: 0 }} borderless={true}>
                    <MenuItem icon="home" onClick={this.onDropdownClick} />
                    <MenuItem content="LoanMan" style={{ fontWeight: "bold" }} />
                    <MenuMenu position="right">
                        <MenuItem content="username" />
                        <MenuItem icon="cog" onClick={this.onDropdownClick} />
                    </MenuMenu>
                </Menu>
                <CutoffPage />
            </Container>
        );
    }

    private onDropdownClick = () => {
        alert("Whoa");
    }

}