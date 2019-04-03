package net.consensys.web3auth.smartcontract.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class Web3AuthPolicyI extends Contract {
    private static final String BINARY = "";

    public static final String FUNC_GETORGANISATIONS = "getOrganisations";

    public static final String FUNC_GETPRIVILEGE = "getPrivilege";

    public static final Event MEMBERADDED_EVENT = new Event("MemberAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<Uint8>(true) {}));
    ;

    public static final Event MEMBERREMOVED_EVENT = new Event("MemberRemoved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<Uint8>(true) {}));
    ;

    @Deprecated
    protected Web3AuthPolicyI(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Web3AuthPolicyI(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Web3AuthPolicyI(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Web3AuthPolicyI(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<List> getOrganisations(String _user) {
        final Function function = new Function(FUNC_GETORGANISATIONS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {}));
        return new RemoteCall<List>(
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteCall<BigInteger> getPrivilege(String _user, byte[] _organisation) {
        final Function function = new Function(FUNC_GETPRIVILEGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_user), 
                new org.web3j.abi.datatypes.generated.Bytes32(_organisation)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public List<MemberAddedEventResponse> getMemberAddedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MEMBERADDED_EVENT, transactionReceipt);
        ArrayList<MemberAddedEventResponse> responses = new ArrayList<MemberAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MemberAddedEventResponse typedResponse = new MemberAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._member = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._organisation = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._privilege = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MemberAddedEventResponse> memberAddedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MemberAddedEventResponse>() {
            @Override
            public MemberAddedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MEMBERADDED_EVENT, log);
                MemberAddedEventResponse typedResponse = new MemberAddedEventResponse();
                typedResponse.log = log;
                typedResponse._member = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._organisation = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._privilege = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MemberAddedEventResponse> memberAddedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MEMBERADDED_EVENT));
        return memberAddedEventObservable(filter);
    }

    public List<MemberRemovedEventResponse> getMemberRemovedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MEMBERREMOVED_EVENT, transactionReceipt);
        ArrayList<MemberRemovedEventResponse> responses = new ArrayList<MemberRemovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MemberRemovedEventResponse typedResponse = new MemberRemovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._member = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._organisation = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._privilege = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MemberRemovedEventResponse> memberRemovedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MemberRemovedEventResponse>() {
            @Override
            public MemberRemovedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MEMBERREMOVED_EVENT, log);
                MemberRemovedEventResponse typedResponse = new MemberRemovedEventResponse();
                typedResponse.log = log;
                typedResponse._member = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._organisation = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._privilege = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MemberRemovedEventResponse> memberRemovedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MEMBERREMOVED_EVENT));
        return memberRemovedEventObservable(filter);
    }

    public static RemoteCall<Web3AuthPolicyI> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Web3AuthPolicyI.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Web3AuthPolicyI> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Web3AuthPolicyI.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Web3AuthPolicyI> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Web3AuthPolicyI.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Web3AuthPolicyI> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Web3AuthPolicyI.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static Web3AuthPolicyI load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Web3AuthPolicyI(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Web3AuthPolicyI load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Web3AuthPolicyI(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Web3AuthPolicyI load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Web3AuthPolicyI(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Web3AuthPolicyI load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Web3AuthPolicyI(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class MemberAddedEventResponse {
        public Log log;

        public String _member;

        public BigInteger _organisation;

        public BigInteger _privilege;
    }

    public static class MemberRemovedEventResponse {
        public Log log;

        public String _member;

        public BigInteger _organisation;

        public BigInteger _privilege;
    }
}
