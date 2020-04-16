import * as React from 'react';
import {
    Button,
    Form,
    FormButton,
    FormInput,
    FormProps,
    FormTextArea,
    InputOnChangeData,
    Message,
    Modal,
    ModalActions,
    ModalContent,
    ModalHeader,
    TextAreaProps,
    FormGroup,
} from 'semantic-ui-react';

import IFormProps from '../../common/IFormProps';
import IFormState from '../../common/IFormState';
import Util from '../../common/Util';
import Client from '../../model/Client';

interface IState extends IFormState<Client> {

}

export default class ClientForm extends React.Component<IFormProps, IState> {

    public state: IState = {
        content: new Client(),
        errorMap: new Map<string, string>(),
        errorMessage: "",
        loading: false
    }

    public render() {
        return (
            <Modal open={this.props.visible} size="small">
                <ModalHeader>{(Util.isBlankOrNullString(this.state.content.id) ? "New" : "Update") + " Client"}</ModalHeader>
                <ModalContent>
                    <Form onSubmit={this.onSubmit}>
                        <FormInput
                            label="First Name"
                            value={this.state.content.firstName}
                            disabled={this.state.loading}
                            fieldname="firstName"
                            onChange={this.onInputChange}
                            error={this.state.errorMap.get("firstName")}
                            maxLength={32}
                        />
                        <FormInput
                            label="Middle Name"
                            value={this.state.content.middleName}
                            disabled={this.state.loading}
                            fieldname="middleName"
                            onChange={this.onInputChange}
                            maxLength={32}
                        />
                        <FormInput
                            label="Last Name"
                            value={this.state.content.lastName}
                            disabled={this.state.loading}
                            fieldname="lastName"
                            onChange={this.onInputChange}
                            error={this.state.errorMap.get("lastName")}
                            maxLength={32}
                        />
                        <FormGroup widths="equal">
                            <FormInput
                                label="Contact No."
                                value={this.state.content.contactNumber}
                                disabled={this.state.loading}
                                fieldname="contactNumber"
                                onChange={this.onInputChange}
                                error={this.state.errorMap.get("contactNumber")}
                                maxLength={64}
                            />
                            <FormInput
                                label="Email Address"
                                value={this.state.content.emailAddress}
                                disabled={this.state.loading}
                                fieldname="emailAddress"
                                onChange={this.onInputChange}
                                maxLength={128}
                            />
                        </FormGroup>
                        <FormTextArea
                            label="Address"
                            value={this.state.content.address}
                            disabled={this.state.loading}
                            fieldname="address"
                            onChange={this.onAddressChange}
                            maxLength={512}
                        />
                        {
                            !Util.isBlankOrNullString(this.state.errorMessage) &&
                            <Message error={true}>
                                {this.state.errorMessage}
                            </Message>
                        }
                        <FormButton
                            style={{ display: "none" }}
                        />
                    </Form>
                </ModalContent>
                <ModalActions>
                    <Button
                        primary={true}
                        content={Util.isBlankOrNullString(this.state.content.id) ? "Create" : "Update"}
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
        if (Util.isBlankOrNullString(content.lastName)) {
            errorMap.set("lastName", "Last name can't be blank.");
        }
        if (Util.isBlankOrNullString(content.firstName)) {
            errorMap.set("firstName", "First name can't be blank.");
        }
        if (Util.isBlankOrNullString(content.contactNumber)) {
            errorMap.set("contactNumber", "Contact number can't be blank.");
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
        Reflect.set(content, data.fieldname, data.value);
        this.setState({ content });
    }

    private onAddressChange = (event: React.FormEvent<HTMLTextAreaElement>, data: TextAreaProps) => {
        const content = this.state.content;
        content.address = data.value as string;
        this.setState({ content });
    }

}