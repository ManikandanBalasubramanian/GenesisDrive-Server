package com.test.db.table;

public class SubscriptionsTable {

  private long id;
  private long userId;
  private String web3StorageAPIKey;
  private String nftStorageAPIKey;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getWeb3StorageAPIKey() {
    return web3StorageAPIKey;
  }

  public void setWeb3StorageAPIKey(String web3StorageAPIKey) {
    this.web3StorageAPIKey = web3StorageAPIKey;
  }

  public String getNftStorageAPIKey() {
    return nftStorageAPIKey;
  }

  public void setNftStorageAPIKey(String nftStorageAPIKey) {
    this.nftStorageAPIKey = nftStorageAPIKey;
  }
}
