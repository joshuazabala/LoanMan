import * as React from 'react';
import { Button, Modal, ModalActions, ModalContent, ModalHeader } from 'semantic-ui-react';

import IFormState from '../../common/IFormState';
import Session from '../../model/Session';

interface IProps {
    onDeactivate(): void,
    onCancel(): void,
    visible: boolean
}

export default class SessionForm extends React.Component<IProps, IFormState<Session>> {

    public state: IFormState<Session> = {
        content: new Session(),
        loading: false,
        errorMap: new Map<string, string>(),
        errorMessage: ""
    }

    public render() {
        return (
            <Modal open={this.props.visible} size="small">
                <ModalHeader>Session Details</ModalHeader>
                <ModalContent>
                    
                </ModalContent>
                <ModalActions>
                    <Button
                        primary={true}
                        content="Deactivate"
                        icon="trash"
                        onClick={this.onDeactivate}
                        loading={this.state.loading}
                        disabled={this.state.loading}
                    />
                    <Button
                        negative={true}
                        content="Close"
                        icon="cancel"
                        onClick={this.props.onCancel}
                        disabled={this.state.loading}
                    />
                </ModalActions>
            </Modal>
        );
    }

    private onDeactivate = () => {
        this.props.onDeactivate();
    }

    private onCancel = () => {
        this.props.onCancel();
    }

}