import * as React from 'react';
import {
    Button,
    DropdownProps,
    Form,
    FormButton,
    FormDropdown,
    FormInput,
    FormProps,
    InputOnChangeData,
    Message,
    Modal,
    ModalActions,
    ModalContent,
    ModalHeader,
} from 'semantic-ui-react';

import { EnumCutoffFrequency } from '../../common/EnumCutoffFrequency';
import IFormProps from '../../common/IFormProps';
import IFormState from '../../common/IFormState';
import Util from '../../common/Util';
import LoanType from '../../model/LoanType';

const paymentFrequencyOptions = [
    {
        key: EnumCutoffFrequency.MONTHLY,
        value: EnumCutoffFrequency.MONTHLY,
        text: "Monthly"
    },
    {
        key: EnumCutoffFrequency.SEMI_MONTHLY,
        value: EnumCutoffFrequency.SEMI_MONTHLY,
        text: "Semi-Monthly"
    }
];

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
                        <FormDropdown
                            label="Payment Frequency"
                            options={paymentFrequencyOptions}
                            selection={true}
                            fluid={true}
                            value={this.state.content.paymentFrequency}
                            onChange={this.onPaymentFrequencyChange}
                            disabled={this.state.loading}
                            error={this.state.errorMap.get("paymentFrequency")}
                        />
                        <FormInput
                            label="Name"
                            value={this.state.content.name}
                            fieldname="name"
                            onChange={this.onInputChange}
                            maxLength="32"
                            disabled={this.state.loading}
                            error={this.state.errorMap.get("name")}
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
        if (Util.isBlankOrNullString(content.name)) {
            errorMap.set("name", "Name is required.");
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
        if (data.fieldname === "name") {
            content.name = data.value;
        } else if (data.fieldname === "description") {
            content.description = data.value;
        }
        this.setState({ content });
    }

    private onPaymentFrequencyChange = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
        const content = this.state.content;
        content.paymentFrequency = data.value as EnumCutoffFrequency;
        this.setState({ content });
    }

}