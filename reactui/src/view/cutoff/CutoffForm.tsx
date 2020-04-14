import * as React from 'react';
import {
    Button,
    DropdownItemProps,
    DropdownProps,
    Form,
    FormButton,
    FormDropdown,
    FormGroup,
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
import Cutoff from '../../model/Cutoff';

interface IState extends IFormState<Cutoff> {
    frequencies: DropdownItemProps[],
    months: DropdownItemProps[],
    cutoffNumbers: DropdownItemProps[]
}

export default class CutoffForm extends React.Component<IFormProps, IFormState<Cutoff>> {

    public state: IState = {
        content: new Cutoff(),
        errorMap: new Map<string, string>(),
        errorMessage: "",
        loading: false,
        frequencies: [
            { key: EnumCutoffFrequency.MONTHLY, value: EnumCutoffFrequency.MONTHLY, text: "Monthly" },
            { key: EnumCutoffFrequency.SEMI_MONTHLY, value: EnumCutoffFrequency.SEMI_MONTHLY, text: "Semi-Monthly" }
        ],
        months: [
            { key: 1, value: 1, text: "January" },
            { key: 2, value: 2, text: "February" },
            { key: 3, value: 3, text: "March" },
            { key: 4, value: 4, text: "April" },
            { key: 5, value: 5, text: "May" },
            { key: 6, value: 6, text: "June" },
            { key: 7, value: 7, text: "July" },
            { key: 8, value: 8, text: "August" },
            { key: 9, value: 9, text: "September" },
            { key: 10, value: 10, text: "October" },
            { key: 11, value: 11, text: "November" },
            { key: 12, value: 12, text: "December" }
        ],
        cutoffNumbers: [
            { key: 1, value: 1, text: "1st" },
            { key: 2, value: 2, text: "2nd" }
        ]
    }

    public render() {
        return (
            <Modal open={this.props.visible} size="small">
                <ModalHeader>{(this.state.content.id === 0 ? "New" : "Update") + " Cutoff"}</ModalHeader>
                <ModalContent>
                    <Form onSubmit={this.onSubmit}>
                        <FormDropdown
                            label="Frequency"
                            options={this.state.frequencies}
                            value={this.state.content.frequency}
                            selection={true}
                            onChange={this.onFrequencyChange}
                            disabled={this.state.loading}
                            error={this.state.errorMap.get("frequency")}
                        />
                        <FormGroup widths="equal">
                            <FormInput
                                label="Year"
                                value={this.state.content.year}
                                onChange={this.onYearChange}
                                fieldname="year"
                                disabled={this.state.loading}
                                error={this.state.errorMap.get("year")}
                            />
                            <FormDropdown
                                label="Month"
                                options={this.state.months}
                                value={this.state.content.month}
                                selection={true}
                                onChange={this.onMonthChange}
                                disabled={this.state.loading}
                                error={this.state.errorMap.get("month")}
                            />
                            {
                                this.state.content.frequency === EnumCutoffFrequency.SEMI_MONTHLY &&
                                <FormDropdown
                                    label="Cutoff No."
                                    options={this.state.cutoffNumbers}
                                    value={this.state.content.cutoffNumber}
                                    selection={true}
                                    onChange={this.onCutoffNumberChange}
                                    disabled={this.state.loading}
                                    error={this.state.errorMap.get("cutoffNumber")}
                                />
                            }
                        </FormGroup>
                        <FormGroup widths="equal">
                            <FormInput 
                                label="Start Date"
                                type="date"
                                value={this.state.content.startDate}
                                disabled={this.state.loading}
                                error={this.state.errorMap.get("startDate")}
                                fieldname="startDate"
                                onChange={this.onDateChange}
                            />
                            <FormInput 
                                label="End Date"
                                type="date"
                                value={this.state.content.endDate}
                                disabled={this.state.loading}
                                error={this.state.errorMap.get("endDate")}
                                fieldname="endDate"
                                onChange={this.onDateChange}
                            />
                        </FormGroup>
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
        if (content.frequency === EnumCutoffFrequency.UNKNOWN) {
            errorMap.set("frequency", "This field is required.");
        }
        if (content.year <= 0) {
            errorMap.set("year", "Invalid value.");
        }
        if (content.month < 1 || content.month > 12) {
            errorMap.set("month", "Invalid value.");
        }
        if (content.cutoffNumber !== 1 && content.cutoffNumber !== 2) {
            errorMap.set("cutoffNumber", "Invalid value.");
        }
        if (content.startDate === undefined || content.startDate === null) {
            errorMap.set("startDate", "This field is required.");
        }
        if (content.endDate === undefined || content.endDate === null) {
            errorMap.set("endDate", "This field is required.");
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

    private onFrequencyChange = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
        const content = this.state.content;
        content.frequency = data.value as EnumCutoffFrequency;
        this.setState({ content });
    }

    private onMonthChange = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
        const content = this.state.content;
        content.month = data.value as number;
        this.setState({ content });
    }

    private onCutoffNumberChange = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
        const content = this.state.content;
        content.cutoffNumber = data.value as number;
        this.setState({ content });
    }

    private onYearChange = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        const content = this.state.content;
        let year = +data.value;
        if (!isNaN(year)) {
            content.year = year;
        }
        this.setState({ content });
    }

    private onDateChange = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        const content = this.state.content;
        if (data.fieldname === "startDate") {
            content.startDate = data.value;
        } else {
            content.endDate = data.value;
        }
        this.setState({ content });
    }

}