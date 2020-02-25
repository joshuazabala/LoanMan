import * as React from 'react';

import GroupForm from './group/GroupForm';

export default class Main extends React.Component {

    private loanTypeFormRef = React.createRef<GroupForm>();

    public render() {
        return (
            <GroupForm 
                onCancelled={this.onCancelled}
                onSaved={this.onSaved}
                visible={true}
                ref={this.loanTypeFormRef}
            />
        );
    }

    public onCancelled = () => {
        alert("onCancelled");
    }

    public onSaved = () => {
        const loanType = this.loanTypeFormRef.current!.state.content;
        alert("Group Type:\r\n" + JSON.stringify(loanType));
    }

}