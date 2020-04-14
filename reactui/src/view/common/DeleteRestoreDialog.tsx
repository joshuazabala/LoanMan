import React from 'react';
import { Button, Modal, ModalActions, ModalContent, ModalHeader } from 'semantic-ui-react';

import { EnumCommonAction } from '../../common/EnumCommonAction';

interface IState {
    id: string | number | undefined,
    header: string,
    message: string,
    mode: EnumCommonAction.DELETE | EnumCommonAction.RESTORE
}

interface IProps {
    onDelete(): void,
    onRestore(): void,
    onCancel(): void,
    size: "small" | "mini" | "tiny" | "large" | "fullscreen" | undefined,
    open: boolean
}

export default class DeleteRestoreDialog extends React.Component<IProps, IState> {

    public state: IState = {
        id: undefined,
        header: "Confirm Action",
        message: "Confirm?",
        mode: EnumCommonAction.DELETE
    }

    public render() {
        return (
            <Modal size={this.props.size} open={this.props.open}>
                <ModalHeader>
                    {this.state.header}
                </ModalHeader>
                <ModalContent>
                    {this.state.message}
                </ModalContent>
                <ModalActions>
                    {
                        this.state.mode === EnumCommonAction.DELETE &&
                        <Button 
                            content="Delete"
                            negative={true}
                            icon="trash"
                            onClick={this.props.onDelete}
                        />
                    }
                    {
                        this.state.mode === EnumCommonAction.RESTORE &&
                        <Button 
                            content="Restore"
                            positive={true}
                            icon="undo"
                            onClick={this.props.onRestore}
                        />
                    }
                    <Button 
                        content="Cancel"
                        icon="cancel"
                        onClick={this.props.onCancel}
                    />
                </ModalActions>
            </Modal>
        );
    }

}