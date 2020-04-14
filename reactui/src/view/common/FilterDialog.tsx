import React from 'react';
import {
    Button,
    Form,
    FormCheckbox,
    FormInput,
    InputOnChangeData,
    Label,
    Modal,
    ModalActions,
    ModalContent,
    ModalHeader,
} from 'semantic-ui-react';

interface IProps {
    onSaveAndSearch(): void,
    onReset(): void,
    onClose(): void,
    open: boolean,
    size?: "small" | "mini" | "tiny" | "large" | "fullscreen" | undefined
}

interface IState {
    showInactive: boolean,
    itemsPerPage: number
}

export default class FilterDialog extends React.Component<IProps, IState> {

    public state: IState = {
        itemsPerPage: 20,
        showInactive: false
    }

    public render() {
        return (
            <Modal size={this.props.size} open={this.props.open}>
                <ModalHeader>
                    Search Filters
                </ModalHeader>
                <ModalContent>
                    <Form>
                        <Label content="Show inactive records?" basic={true} style={{ border: "none", paddingLeft: 0 }} />
                        <FormCheckbox 
                            inline={true}
                            toggle={true}
                            checked={this.state.showInactive}
                            label={this.state.showInactive ? "Yes" : "No"}
                            onClick={this.onShowInactiveClick}
                        />
                        <FormInput 
                            label="No. of items per page"
                            type="number"
                            width={4}
                            inline={true}
                            value={this.state.itemsPerPage}
                            onChange={this.onItemsPerPageChange}
                        />
                    </Form>
                </ModalContent>
                <ModalActions>
                    <Button 
                        content={"Save & Search"}
                        icon="search"
                        onClick={this.props.onSaveAndSearch}
                        primary={true}
                    />
                    <Button 
                        content="Reset"
                        icon="undo"
                        onClick={this.props.onReset}
                    />
                    <Button 
                        content="Close"
                        icon="cancel"
                        onClick={this.props.onClose}
                    />
                </ModalActions>
            </Modal>
        );
    }

    private onShowInactiveClick = () => {
        this.setState({ showInactive: !this.state.showInactive });
    }

    private onItemsPerPageChange = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        const mockObject: {itemsPerPage: number} = {itemsPerPage: 0};
        Reflect.set(mockObject, "itemsPerPage", data.value);
        this.setState({ itemsPerPage: mockObject.itemsPerPage });
    }

}