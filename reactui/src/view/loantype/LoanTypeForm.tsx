import * as React from 'react';
import {
    Button,
    Form,
    FormButton,
    FormInput,
    FormProps,
    InputOnChangeData,
    Message,
    Modal,
    ModalActions,
    ModalContent,
    ModalHeader,
} from 'semantic-ui-react';

import IFormProps from '../../common/IFormProps';
import IFormState from '../../common/IFormState';
import Util from '../../common/Util';
import LoanType from '../../model/LoanType';

export default class LoanTypeForm extends React.Component<IFormProps, IFormState<LoanType>> {

    public state: IFormState<LoanType> = {
        content: new LoanType(),
        loading: false,
        errorMap: new Map<string, string>(),
        errorMessage: ""
    }

    public render() {
        return (
            <Modal open={this.props.visible} size="small">
                <ModalHeader>{(this.state.content.id === 0 ? "New" : "Update") + " Loan Type"}</ModalHeader>
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
            content.code = data.value;
        } else if (data.fieldname === "description") {
            content.description = data.value;
        }
        this.setState({ content });
    }

}