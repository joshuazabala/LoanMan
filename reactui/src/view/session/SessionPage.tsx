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
import Session from '../../model/Session';
import SessionForm from './SessionForm';

interface IState extends IPageState<Session> {
    deleteModalVisible: boolean,
    selectedSessionId: string
}

export default class SessionPage extends React.Component {

    public state: IState = {
        contents: new Array<Session>(),
        name: "sessionPage",
        selectedId: 0,
        loading: false,
        pageStat: new PageStat(),
        queryString: "",
        formVisible: false,
        deleteModalVisible: false,
        initialized: false,
        selectedSessionId: ""
    }

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

    private formRef = React.createRef<SessionForm>();

    public render() {
        return (
            <Container fluid={true}>
                <Grid>
                    <GridRow style={{ paddingBottom: 2 }}>
                        <GridColumn>
                            <Header as="h2">Sessions</Header>
                        </GridColumn>
                    </GridRow>
                    <GridRow columns={2} style={{ paddingBottom: 2, paddingTop: 2 }}>
                        <GridColumn width={8}>
                            <Button icon="list" primary={true} content="View Details" disabled={this.state.loading || Util.isBlankOrNullString(this.state.selectedSessionId)} onClick={this.onViewDetails} />
                            <Button icon="trash" negative={true} content="Deactivate" disabled={this.state.loading || Util.isBlankOrNullString(this.state.selectedSessionId)} onClick={this.onDeactivate} />
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
                                        <TableHeaderCell width={3}>Username</TableHeaderCell>
                                        <TableHeaderCell width={2}>Status</TableHeaderCell>
                                        <TableHeaderCell width={3}>Origin</TableHeaderCell>
                                        <TableHeaderCell width={2}>Start Time</TableHeaderCell>
                                        <TableHeaderCell width={2}>Last Activity Time</TableHeaderCell>
                                        <TableHeaderCell width={4}>Remarks</TableHeaderCell>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {
                                        this.state.contents.map((item, index) => {
                                            return (
                                                <TableRow key={index} data-id={item.id} onClick={this.onRowClick} active={this.state.selectedSessionId === item.id}>
                                                    <TableCell>{item.username}</TableCell>
                                                    <TableCell>{item.status}</TableCell>
                                                    <TableCell>{item.origin}</TableCell>
                                                    <TableCell>{item.loginTime}</TableCell>
                                                    <TableCell>{item.lastActivityTime}</TableCell>
                                                    <TableCell>{item.remarks}</TableCell>
                                                </TableRow>
                                            )
                                        })
                                    }
                                </TableBody>
                                <TableFooter>
                                    <TableRow>
                                        <TableHeaderCell colSpan={6} textAlign="right">
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

                    <Modal open={this.state.deleteModalVisible} size="small">
                        <ModalHeader>Confirm Action</ModalHeader>
                        <ModalContent>Deactivate selected session?</ModalContent>
                        <ModalActions>
                            <Button
                                negative={true}
                                content="Deactivate"
                                onClick={this.onDeactivate}
                                disabled={this.state.loading}
                            />
                            <Button
                                content="Cancel"
                                onClick={this.onCancelDeactivate}
                                disabled={this.state.loading}
                            />
                        </ModalActions>
                        <Loader active={this.state.loading} />
                    </Modal>

                </Grid>

                <SessionForm 
                    onCancel={this.onFormCancel}
                    onDeactivate={this.onFormDeactivate}
                    visible={this.state.formVisible}
                />

            </Container>
        );
    }

    private onRowClick = (event: any) => {
        const selectedSessionId = event.currentTarget.getAttribute("data-id") as string;
        this.setState({ selectedSessionId });
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

        this.setState(
            {
                loading: true
            },
            () => {
                fetchPost<PagedSearchRequest, PagedSearchResponse<Session>>("/session/search", requestParam)
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

        fetchPost<{ id: string }, Session>("/session/findById", requestParam)
            .then(item => {
                this.setState(
                    { formVisible: true },
                    () => {
                        this.formRef.current!.setState({ content: item });
                    }
                );
            });
    }

    private onFormCancel = () => {
        this.formRef.current!.setState(
            { content: new Session() },
            () => {
                this.setState({ formVisible: false });
            }
        )
    }

    private onViewDetails = () => {
        if (Util.isBlankOrNullString(this.state.selectedSessionId)) {
            return;
        }

        this.showForm(this.state.selectedSessionId);
    }

    private onDeactivate = () => {
        this.setState({ deleteModalVisible: true });
    }

    private onCancelDeactivate = () => {
        this.setState({ deleteModalVisible: false });
    }

    private onFormDeactivate = () => {
        
    }

    private deactivate = (sessionId: string) => {
        const requestParam = new RequestContainer<string>();
        requestParam.content = sessionId;

        this.setState(
            { loading: true },
            () => {
                fetchPost<RequestContainer<string>, ResponseContainer<boolean>>("/userprofile/deactivate", requestParam)
                    .then(response => {
                        if (response.status === EnumResponseStatus.SUCCESSFUL) {
                            let contents = this.state.contents;
                            contents = contents.filter(item => item.id !== sessionId);
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