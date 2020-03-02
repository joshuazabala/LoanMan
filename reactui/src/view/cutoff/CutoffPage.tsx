import * as React from 'react';
import {
    Button,
    DropdownItemProps,
    Form,
    FormInput,
    FormProps,
    Grid,
    GridColumn,
    GridRow,
    Header,
    Icon,
    InputOnChangeData,
    Loader,
    Modal,
    ModalActions,
    ModalContent,
    ModalHeader,
    Pagination,
    PaginationProps,
    Table,
    TableBody,
    TableCell,
    TableFooter,
    TableHeader,
    TableHeaderCell,
    TableRow,
} from 'semantic-ui-react';

import { EnumCutoffFrequency } from '../../common/EnumCutoffFrequency';
import { EnumCutoffStatus } from '../../common/EnumCutoffStatus';
import { EnumResponseStatus } from '../../common/EnumResponseStatus';
import IPageState from '../../common/IPageState';
import PagedSearchRequest from '../../common/PagedSearchRequest';
import PagedSearchResponse from '../../common/PagedSearchResponse';
import PageStat from '../../common/PageStat';
import { fetchPost } from '../../common/Request';
import RequestContainer from '../../common/RequestContainer';
import ResponseContainer from '../../common/ResponseContainer';
import Util from '../../common/Util';
import Cutoff from '../../model/Cutoff';
import CutoffForm from './CutoffForm';

interface IState extends IPageState<Cutoff> {
    deleteModalVisible: boolean,
    year: number,
    frequency: EnumCutoffFrequency
    frequencies: DropdownItemProps[]
}

export default class CutoffPage extends React.Component<any, IState> {

    public state: IState = {
        contents: new Array<Cutoff>(),
        formVisible: false,
        initialized: false,
        loading: false,
        name: "cutoffPage",
        pageStat: new PageStat(),
        queryString: "",
        selectedId: 0,
        deleteModalVisible: false,
        year: 2020,
        frequency: EnumCutoffFrequency.UNKNOWN,
        frequencies: [
            { key: EnumCutoffFrequency.MONTHLY, value: EnumCutoffFrequency.MONTHLY, text: "Monthly" },
            { key: EnumCutoffFrequency.SEMI_MONTHLY, value: EnumCutoffFrequency.SEMI_MONTHLY, text: "Semi-Monthly" }
        ]
    }

    public constructor(props: any) {
        super(props);

        this.monthMap = new Map<number, string>();
        this.monthMap.set(1, "January");
        this.monthMap.set(2, "February");
        this.monthMap.set(3, "March");
        this.monthMap.set(4, "April");
        this.monthMap.set(5, "May");
        this.monthMap.set(6, "June");
        this.monthMap.set(7, "July");
        this.monthMap.set(8, "August");
        this.monthMap.set(9, "September");
        this.monthMap.set(10, "October");
        this.monthMap.set(11, "November");
        this.monthMap.set(12, "December");
    }

    private formRef = React.createRef<CutoffForm>();
    private monthMap: Map<number, string>;

    public componentDidMount() {
        if (!this.state.initialized) {
            this.setState(
                { initialized: true },
                () => {
                    this.search();
                }
            );
        }
    }

    public render() {
        return (
            <Grid container={true}>
                <GridRow>
                    <GridColumn>
                        <Header as="h2">Cutoffs</Header>
                    </GridColumn>
                </GridRow>
                <GridRow columns={2} style={{ paddingBottom: 2, paddingTop: 2 }}>
                    <GridColumn width={8}>
                        <Button icon="add" primary={true} content="New" disabled={this.state.loading} onClick={this.onAdd} />
                        {this.state.selectedId !== 0 && <Button icon="edit" content="Edit" disabled={this.state.loading} onClick={this.onEdit} />}
                        {this.state.selectedId !== 0 && <Button icon="trash" content="Delete" disabled={this.state.loading} onClick={this.onDelete} />}
                    </GridColumn>
                    <GridColumn textAlign="right">
                        <Form onSubmit={this.onFormSubmit}>
                            <FormInput
                                disabled={this.state.loading}
                                placeholder="Search"
                                action={{ icon: "search", content: "Search" }}
                                value={this.state.queryString}
                                onChange={this.onQueryStringChanged}
                            />
                        </Form>
                    </GridColumn>
                </GridRow>
                <GridRow style={{ paddingBottom: 40, paddingTop: 2 }}>
                    <GridColumn>
                        <Table celled={true} striped={true} selectable={true}>
                            <TableHeader>
                                <TableRow>
                                    <TableHeaderCell width={4}>Cutoff For</TableHeaderCell>
                                    <TableHeaderCell width={4}>Date Range</TableHeaderCell>
                                    <TableHeaderCell width={4}>Frequency</TableHeaderCell>
                                    <TableHeaderCell width={4}>Status</TableHeaderCell>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {
                                    this.state.contents.map((item, index) => {
                                        return (
                                            <TableRow key={index} data-loantypeid={item.id} onClick={this.onRowClick} active={this.state.selectedId === item.id}>
                                                <TableCell>{this.createCutoffNoText(item)}</TableCell>
                                                <TableCell>{item.startDate + " to " + item.endDate}</TableCell>
                                                <TableCell>{item.frequency}</TableCell>
                                                <TableCell>{item.status}</TableCell>
                                            </TableRow>
                                        )
                                    })
                                }
                            </TableBody>
                            <TableFooter>
                                <TableRow>
                                    <TableHeaderCell colSpan={4} textAlign="right">
                                        <Loader active={this.state.loading} />
                                        <Pagination 
                                            disabled={this.state.loading}
                                            onPageChange={this.onPageChange}
                                            defaultActivePage={this.state.pageStat.currentPage} 
                                            totalPages={this.state.pageStat.totalPageCount} 
                                            ellipsisItem={{ content: <Icon name='ellipsis horizontal' />, icon: true }}
                                            firstItem={{ content: <Icon name='angle double left' />, icon: true }}
                                            lastItem={{ content: <Icon name='angle double right' />, icon: true }}
                                            prevItem={{ content: <Icon name='angle left' />, icon: true }}
                                            nextItem={{ content: <Icon name='angle right' />, icon: true }}
                                        />
                                    </TableHeaderCell>
                                </TableRow>
                            </TableFooter>
                        </Table>
                    </GridColumn>
                </GridRow>
                
                <CutoffForm 
                    visible={this.state.formVisible}
                    ref={this.formRef}
                    onCancelled={this.onFormCancelled}
                    onSaved={this.onFormSaved}
                />

                <Modal open={this.state.deleteModalVisible} size="small">
                    <ModalHeader>Confirm Action</ModalHeader>
                    <ModalContent>Delete selected cutoff?</ModalContent>
                    <ModalActions>
                        <Button 
                            negative={true}
                            content="Delete"
                            onClick={this.delete}
                            disabled={this.state.loading}
                        />
                        <Button 
                            content="Cancel"
                            onClick={this.onCancelDelete}
                            disabled={this.state.loading}
                        />
                    </ModalActions>
                    <Loader active={this.state.loading} />
                </Modal>

            </Grid>
        );
    }

    private createCutoffNoText = (cutoff: Cutoff) => {
        let text = this.monthMap.get(cutoff.month)! + " " + cutoff.year;
        if (cutoff.frequency === EnumCutoffFrequency.SEMI_MONTHLY) {
            text += ", " + (cutoff.cutoffNumber === 1 ? "1st" : "2nd");
        }
        return text;
    }

    private onRowClick = (event: any) => {
        const loanTypeId = event.currentTarget.getAttribute("data-loantypeid") as number;
        this.setState({ selectedId: +loanTypeId });
    }

    private onQueryStringChanged = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        this.setState({ queryString: data.value });
    }

    private onFormSubmit = (event: React.FormEvent<HTMLFormElement>, data: FormProps) => {
        event.preventDefault();
        const pageStat = this.state.pageStat;
        pageStat.currentPage = 1;
        this.setState(
            { pageStat, selectedId: 0 },
            () => {
                this.search();
            }
        );
    }

    private onPageChange = (event: React.MouseEvent<HTMLElement, MouseEvent>, data: PaginationProps) => {
        const pageStat = this.state.pageStat;
        pageStat.currentPage = data.activePage as number;
        this.setState(
            { pageStat },
            () => {
                this.search();
            }
        );
    }

    private search = () => {
        const requestParam = new PagedSearchRequest();
        requestParam.includeInactive = false;
        requestParam.pageNumber = this.state.pageStat.currentPage;
        requestParam.pageSize = this.state.pageStat.pageSize;
        requestParam.queryString = this.state.queryString;
        requestParam.otherData = {
            year: this.state.year,
            statuses: [ EnumCutoffStatus.DRAFT, EnumCutoffStatus.POSTED ].join(","),
            frequency: this.state.frequency
        };

        this.setState(
            {
                loading: true
            },
            () => {
                fetchPost<PagedSearchRequest, PagedSearchResponse<Cutoff>>("/cutoff/search", requestParam)
                .then(result => {
                    if (result.status === EnumResponseStatus.SUCCESSFUL) {
                        const pageStat = this.state.pageStat;
                        pageStat.totalPageCount = result.totalPageCount;
                        this.setState({
                            contents: result.content,
                            loading: false,
                            pageStat
                        });
                    } else {
                        this.setState(
                            { loading: false },
                            () => {
                                alert("Error: " + result.message);
                            }
                        );
                    }
                });
            }
        );
    }

    private showForm = (id: number) => {
        const requestParam = { id };

        fetchPost<{id: number}, Cutoff>("/cutoff/findById", requestParam)
        .then(item => {
            this.setState(
                { formVisible: true },
                () => {
                    this.formRef.current!.setState({ content: item });
                }
            );
        });
    }

    private onFormCancelled = () => {
        this.setState({ formVisible: false });
    }

    private onFormSaved = () => {
        this.formRef.current!.setState(
            { loading: true },
            () => {
                const content = this.formRef.current!.state.content;
                const requestParam = new RequestContainer<Cutoff>();
                requestParam.content = content;

                fetchPost<RequestContainer<Cutoff>, ResponseContainer<Cutoff>>("/cutoff/save", requestParam)
                .then(response => {
                    this.formRef.current!.setState(
                        { loading: false },
                        () => {
                            if (response.status === EnumResponseStatus.SUCCESSFUL) {
                                const contents = this.state.contents;
                                const index = contents.findIndex(item => item.id === response.content.id);
                                if (index === -1) {
                                    contents.unshift(response.content);
                                } else {
                                    contents[index] = response.content;
                                }
    
                                this.setState({
                                    formVisible: false,
                                    contents
                                });
                            } else {
                                this.formRef.current!.setState({
                                    loading: false,
                                    errorMap: Util.objectToMap(response.errorMap),
                                    errorMessage: response.message
                                });
                            }
                        }
                    );
                });
            }
        );
    }

    private onAdd = () => {
        this.showForm(0);
    }

    private onEdit = () => {
        if (this.state.selectedId === 0) {
            return;
        }

        this.showForm(this.state.selectedId);
    }

    private onDelete = () => {
        this.setState({ deleteModalVisible: true });
    }

    private onCancelDelete = () => {
        this.setState({ deleteModalVisible: false });
    }

    private delete = () => {
        const requestParam = new RequestContainer<number>();
        requestParam.content = this.state.selectedId;

        this.setState(
            { loading: true },
            () => {
                fetchPost<RequestContainer<number>, ResponseContainer<boolean>>("/cutoff/delete", requestParam)
                .then(response => {
                    if (response.status === EnumResponseStatus.SUCCESSFUL) {
                        let contents = this.state.contents;
                        contents = contents.filter(item => item.id !== this.state.selectedId);
                        this.setState({
                            contents: contents,
                            selectedId: 0,
                            deleteModalVisible: false,
                            loading: false
                        });
                    } else {
                        alert("Error: " + response.message);
                    }
                });
            }
        );
    }

}