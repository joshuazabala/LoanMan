import * as React from 'react';
import IFormProps from '../../common/IFormProps';
import IFormState from '../../common/IFormState';
import GroupType from '../../model/GroupType';
import { Modal, ModalHeader, ModalContent, Form, FormInput, FormButton, ModalActions, Button, FormProps, InputOnChangeData } from 'semantic-ui-react';
import Util from '../../common/Util';

export default class GroupTypeForm extends React.Component<IFormProps, IFormState<GroupType>> {

    public state: IFormState<GroupType> = {
        content: new GroupType(),
        loading: false,
        errorMap: new Map<string, string>(),
        errorMessage: ""
    }

    public render() {
        return (
            <Modal open={this.props.visible} size="small">
                <ModalHeader>{(this.state.content.id === 0 ? "New" : "Update") + " Group Type"}</ModalHeader>
                <ModalContent>
                    <Form onSubmit={this.onSubmit}>
                        <FormInput
                            label="Code"
                            value={this.state.content.code}
                            fieldname="code"
                            onChange={this.onInputChange}
                            maxLength="16"
                            width={8}
                            disabled={this.state.loading}
                            error={this.state.errorMap.get("code")}
                        />
                        <FormInput
                            label="Description"
                            value={this.state.content.description}
                            onChange={this.onInputChange}
                            fieldname="description"
                            maxLength="512"
                            disabled={this.state.loading}
                            error={this.state.errorMap.get("description")}
                        />
                        <FormButton
                            style={{ display: "none" }}
                        />
                    </Form>
                </ModalContent>
                <ModalActions>
                    <Button
                        primary={true}
                        content={this.state.content.id === 0 ? "Create" : "Update"}
                        icon="save"
                        onClick={this.onSave}
                        loading={this.state.loading}
                        disabled={this.state.loading}
                    />
                    <Button
                        negative={true}
                        content="Cancel"
                        icon="cancel"
                        onClick={this.props.onCancelled}
                        disabled={this.state.loading}
                    />
                </ModalActions>
            </Modal>
        );
    }

    public onSubmit = (event: React.FormEvent<HTMLFormElement>, data: FormProps) => {
        event.preventDefault();
        this.onSave();
    }

    private onSave = () => {
        const content = this.state.content;
        const errorMap = new Map<string, string>();
        if (Util.isBlankOrNullString(content.code)) {
            errorMap.set("code", "Code is required.");
        }
        if (Util.isBlankOrNullString(content.description)) {
            errorMap.set("description", "Description is required.");
        }
        this.setState(
            { errorMap },
            () => {
                if (errorMap.size === 0) {
                    this.props.onSaved();
                }
            }
        );
    }

    private onInputChange = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        const content = this.state.content;
        if (data.fieldname === "code") {
            content.code = data.value.toUpperCase();
        } else if (data.fieldname === "description") {
            content.description = data.value;
        }
        this.setState({ content });
    }

}