import * as React from 'react';
import {
    Button,
    Container,
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

import { EnumResponseStatus } from '../../common/EnumResponseStatus';
import IPageState from '../../common/IPageState';
import PagedSearchRequest from '../../common/PagedSearchRequest';
import PagedSearchResponse from '../../common/PagedSearchResponse';
import PageStat from '../../common/PageStat';
import { fetchPost } from '../../common/Request';
import RequestContainer from '../../common/RequestContainer';
import ResponseContainer from '../../common/ResponseContainer';
import Util from '../../common/Util';
import Client from '../../model/Client';
import ClientForm from './ClientForm';

interface IState extends IPageState<Client> {
    deleteModalVisible: boolean,
    selectedClientNumber: string
}

export default class ClientPage extends React.Component {

    public state: IState = {
        contents: new Array<Client>(),
        deleteModalVisible: false,
        formVisible: false,
        initialized: false,
        loading: false,
        name: "clientPage",
        pageStat: new PageStat(),
        queryString: "",
        selectedId: 0,
        selectedClientNumber: ""
    }

    private formRef = React.createRef<ClientForm>();

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
            <Container fluid={true}>
                <Grid>
                    <GridRow style={{ paddingBottom: 2 }}>
                        <GridColumn>
                            <Header as="h2">Clients</Header>
                        </GridColumn>
                    </GridRow>
                    <GridRow columns={2} style={{ paddingBottom: 2, paddingTop: 2 }}>
                        <GridColumn width={8}>
                            <Button icon="add" primary={true} content="New" disabled={this.state.loading} onClick={this.onAdd} />
                            <Button icon="edit" primary={true} content="Edit" disabled={this.state.loading || Util.isBlankOrNullString(this.state.selectedClientNumber)} onClick={this.onEdit} />
                            <Button icon="trash" negative={true} content="Delete" disabled={this.state.loading || Util.isBlankOrNullString(this.state.selectedClientNumber)} onClick={this.onDelete} />
                        </GridColumn>
                        <GridColumn textAlign="right">
                            <Form onSubmit={this.onFormSubmit}>
                                <FormInput
                                    disabled={this.state.loading}
                                    placeholder="Search"
                                    action={{ icon: "search", content: "Search", primary: true }}
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
                                        <TableHeaderCell width={2}>Client No.</TableHeaderCell>
                                        <TableHeaderCell width={4}>Name</TableHeaderCell>
                                        <TableHeaderCell width={2}>Contact No.</TableHeaderCell>
                                        <TableHeaderCell width={3}>Email Address</TableHeaderCell>
                                        <TableHeaderCell width={5}>Address</TableHeaderCell>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {
                                        this.state.contents.map((item, index) => {
                                            return (
                                                <TableRow key={index} data-id={item.id} onClick={this.onRowClick} active={this.state.selectedClientNumber === item.id}>
                                                    <TableCell>{item.id}</TableCell>
                                                    <TableCell>{item.lastName + ", " + item.firstName + (Util.isBlankOrNullString(item.middleName) ? "" : " " + item.middleName)}</TableCell>
                                                    <TableCell>{item.contactNumber}</TableCell>
                                                    <TableCell>{item.emailAddress}</TableCell>
                                                    <TableCell>{item.address}</TableCell>
                                                </TableRow>
                                            )
                                        })
                                    }
                                </TableBody>
                                <TableFooter>
                                    <TableRow>
                                        <TableHeaderCell colSpan={5} textAlign="right">
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

                    <ClientForm
                        visible={this.state.formVisible}
                        ref={this.formRef}
                        onCancelled={this.onFormCancelled}
                        onSaved={this.onFormSaved}
                    />

                    <Modal open={this.state.deleteModalVisible} size="small">
                        <ModalHeader>Confirm Action</ModalHeader>
                        <ModalContent>Delete selected client?</ModalContent>
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
            </Container>
        );
    }

    private onRowClick = (event: any) => {
        const selectedClientNumber = event.currentTarget.getAttribute("data-id") as string;
        this.setState({ selectedClientNumber });
    }

    private onQueryStringChanged = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        this.setState({ queryString: data.value });
    }

    private onFormSubmit = (event: React.FormEvent<HTMLFormElement>, data: FormProps) => {
        event.preventDefault();
        const pageStat = this.state.pageStat;
        pageStat.currentPage = 1;
        this.setState(
            { pageStat, selectedClientNumber: "" },
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

        this.setState(
            {
                loading: true
            },
            () => {
                fetchPost<PagedSearchRequest, PagedSearchResponse<Client>>("/client/search", requestParam)
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

    private showForm = (id: string) => {
        const requestParam = { id };

        fetchPost<{ id: string }, Client>("/client/findById", requestParam)
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
                const requestParam = new RequestContainer<Client>();
                requestParam.content = content;

                fetchPost<RequestContainer<Client>, ResponseContainer<Client>>("/client/save", requestParam)
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
        this.showForm("");
    }

    private onEdit = () => {
        if (Util.isBlankOrNullString(this.state.selectedClientNumber)) {
            return;
        }

        this.showForm(this.state.selectedClientNumber);
    }

    private onDelete = () => {
        this.setState({ deleteModalVisible: true });
    }

    private onCancelDelete = () => {
        this.setState({ deleteModalVisible: false });
    }

    private delete = () => {
        const requestParam = new RequestContainer<string>();
        requestParam.content = this.state.selectedClientNumber;

        this.setState(
            { loading: true },
            () => {
                fetchPost<RequestContainer<string>, ResponseContainer<boolean>>("/client/delete", requestParam)
                    .then(response => {
                        if (response.status === EnumResponseStatus.SUCCESSFUL) {
                            let contents = this.state.contents;
                            contents = contents.filter(item => item.id !== this.state.selectedClientNumber);
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