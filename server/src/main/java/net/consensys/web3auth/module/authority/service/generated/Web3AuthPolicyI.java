package net.consensys.web3auth.module.authority.service.generated;

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
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.5.0.
 */
public class Web3AuthPolicyI extends Contract {
    private static final String BINARY = "";

    public static final String FUNC_GETORGANISATIONS = "getOrganisations";

    public static final String FUNC_GETPRIVILEGE = "getPrivilege";

    public static final Event MEMBERENABLED_EVENT = new Event("MemberEnabled", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Uint8>(true) {}));
    ;

    public static final Event MEMBERDISABLED_EVENT = new Event("MemberDisabled", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Uint8>(true) {}));
    ;

    protected Web3AuthPolicyI(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Web3AuthPolicyI(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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

    public List<MemberEnabledEventResponse> getMemberEnabledEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MEMBERENABLED_EVENT, transactionReceipt);
        ArrayList<MemberEnabledEventResponse> responses = new ArrayList<MemberEnabledEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MemberEnabledEventResponse typedResponse = new MemberEnabledEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._member = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._organisation = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._privilege = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MemberEnabledEventResponse> memberEnabledEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MemberEnabledEventResponse>() {
            @Override
            public MemberEnabledEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MEMBERENABLED_EVENT, log);
                MemberEnabledEventResponse typedResponse = new MemberEnabledEventResponse();
                typedResponse.log = log;
                typedResponse._member = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._organisation = (byte[]) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._privilege = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MemberEnabledEventResponse> memberEnabledEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MEMBERENABLED_EVENT));
        return memberEnabledEventObservable(filter);
    }

    public List<MemberDisabledEventResponse> getMemberDisabledEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MEMBERDISABLED_EVENT, transactionReceipt);
        ArrayList<MemberDisabledEventResponse> responses = new ArrayList<MemberDisabledEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MemberDisabledEventResponse typedResponse = new MemberDisabledEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._member = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._organisation = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._privilege = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MemberDisabledEventResponse> memberDisabledEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MemberDisabledEventResponse>() {
            @Override
            public MemberDisabledEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MEMBERDISABLED_EVENT, log);
                MemberDisabledEventResponse typedResponse = new MemberDisabledEventResponse();
                typedResponse.log = log;
                typedResponse._member = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._organisation = (byte[]) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._privilege = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MemberDisabledEventResponse> memberDisabledEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MEMBERDISABLED_EVENT));
        return memberDisabledEventObservable(filter);
    }

    public static RemoteCall<Web3AuthPolicyI> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Web3AuthPolicyI.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Web3AuthPolicyI> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Web3AuthPolicyI.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static Web3AuthPolicyI load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Web3AuthPolicyI(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Web3AuthPolicyI load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Web3AuthPolicyI(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class MemberEnabledEventResponse {
        public Log log;

        public String _member;

        public byte[] _organisation;

        public BigInteger _privilege;
    }

    public static class MemberDisabledEventResponse {
        public Log log;

        public String _member;

        public byte[] _organisation;

        public BigInteger _privilege;
    }
}
