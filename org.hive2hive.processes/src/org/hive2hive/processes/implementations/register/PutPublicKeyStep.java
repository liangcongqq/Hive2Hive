package org.hive2hive.processes.implementations.register;

import org.hive2hive.core.H2HConstants;
import org.hive2hive.core.model.UserProfile;
import org.hive2hive.core.model.UserPublicKey;
import org.hive2hive.core.network.NetworkManager;
import org.hive2hive.processes.framework.ProcessUtil;
import org.hive2hive.processes.framework.RollbackReason;
import org.hive2hive.processes.framework.exceptions.InvalidProcessStateException;
import org.hive2hive.processes.implementations.common.base.BasePutProcessStep;

public class PutPublicKeyStep extends BasePutProcessStep {

	private final UserProfile profile;
	
	private boolean isPutCompleted;
	private boolean isPutFailed;

	public PutPublicKeyStep(UserProfile profile, NetworkManager networkManager) {
		super(networkManager);
		this.profile = profile;
	}

	@Override
	protected void doExecute() throws InvalidProcessStateException {

		UserPublicKey publicKey = new UserPublicKey(profile.getEncryptionKeys()
				.getPublic());

		put(profile.getUserId(), H2HConstants.USER_PUBLIC_KEY, publicKey,
				profile.getProtectionKeys());
		
		// wait for PUT to complete
		while (isPutCompleted == false) {
			ProcessUtil.wait(this);
		}
		
		if (isPutFailed) {
			cancel(new RollbackReason(this, "Put failed."));
		}
	}

	@Override
	public void onPutSuccess() {
		isPutCompleted = true;
	}

	@Override
	public void onPutFailure() {
		isPutCompleted = true;
		isPutFailed = true;
	}

	@Override
	public void onRemoveSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemoveFailure() {
		// TODO Auto-generated method stub

	}

}
