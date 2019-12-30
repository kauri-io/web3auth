package net.consensys.web3auth.service.wallet.integration.eventeum;

import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;

import lombok.extern.slf4j.Slf4j;
import net.consensys.eventeum.dto.block.BlockDetails;
import net.consensys.eventeum.dto.event.ContractEventDetails;
import net.consensys.eventeum.dto.transaction.TransactionDetails;
import net.consensys.eventeum.integration.broadcast.blockchain.ListenerInvokingBlockchainEventBroadcaster.OnBlockchainEventListener;
import net.consensys.web3auth.service.wallet.WalletProcessorService;
import net.consensys.web3auth.service.wallet.integration.WalletIntegration;
import net.consensys.web3auth.smartcontract.generated.GnosisSafe;
import net.consensys.web3auth.smartcontract.generated.ProxyFactory;

@Component
@Slf4j
public class BlockchainEventListener implements OnBlockchainEventListener {

    private final WalletProcessorService processor;
    private final WalletIntegration integration;
    
    public BlockchainEventListener(WalletProcessorService processor, WalletIntegration integration) {
        this.processor = processor;
        this.integration = integration;
    }

    @Override
    public void onContractEvent(ContractEventDetails contractEventDetails) {
        log.debug("Contract Event received: {}", contractEventDetails);
        
        if(contractEventDetails.getEventSpecificationSignature().equals(EventEncoder.encode(GnosisSafe.ADDEDOWNER_EVENT))) {
            processor.onKeyAdded(
                    contractEventDetails.getAddress().toLowerCase(), 
                    contractEventDetails.getNonIndexedParameters().get(0).getValueString().toLowerCase());
            
        } else if(contractEventDetails.getEventSpecificationSignature().equals(EventEncoder.encode(GnosisSafe.REMOVEDOWNER_EVENT))) {
            processor.onKeyRemoved(
                    contractEventDetails.getAddress().toLowerCase(), 
                    contractEventDetails.getNonIndexedParameters().get(0).getValueString().toLowerCase());
            
        } else if(contractEventDetails.getEventSpecificationSignature().equals(EventEncoder.encode(ProxyFactory.PROXYCREATION_EVENT))) {
            String wallet = contractEventDetails.getNonIndexedParameters().get(0).getValueString();
            String owner = integration.getOwners(wallet).get(0);
            processor.onKeyNewWallet(wallet.toLowerCase(), owner.toLowerCase());
        }
    }
    
    @Override
    public void onNewBlock(BlockDetails blockDetails) {
        //DO NOTHING
    }


    @Override
    public void onTransactionEvent(TransactionDetails transactionDetails) {
        //DO NOTHING
    }
}
