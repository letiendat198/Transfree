# Transfree (WIP)
File transfer over LAN for Windows and Android

## Features

- Transfer files over LAN
- Support Hotspot mode for even faster transfer (TBA)

## Server behavior

### Payload
```access transformers
[TYPE - 3 bytes][LENGTH - 4 bytes][DATA]
```
- Type: One of the following:
  + ACK: Acknowledge `ACK0000`
  + RFS: Refuse `RFS0000`
  + REQ: Request `REQ[LENGTH][HEADERS]`
  + BGN: Begin file transfer `BGN[LENGTH][HEADERS]`
  + BIN: Binary data `BIN[LENGTH][BYTES]`
  + COM: Complete `COM0000`
  + EOF: End of file - End file transfer `EOF0000`
  + END: Close connection `END0000`

- LENGTH : Length of data section in hexadecimal string
- DATA: One of the following type
    + Header: Base64 encoded string with following structure
    ```access transformers
    Header1: Value1\n
    Header2: Value2\n
    ...
    ```
    + Binary: Raw binary data

### Protocol
A file upload is done by the following steps:
- Step 1: Ask for confirmation
  - Client: Send `REQ` with header that include device host name under `name`
  - Server: Await confirmation from user then return either `ATH` with `sessionID` or `RFS`
- Step 2: Send file details
  - Client: Send `BGN` with `sessionID`, `fileName`, `size` as header
  - Server: Return `ACK`, enter file transfer mode. In this mode only `BIN` and `EOF` is processed. Or return `RFS` if 
`sessionID` does not match
- Step 3: Send file data
  - Client: Continuously send `BIN` with raw binary. **Raw binary size must not exceed (MAX_PAYLOAD_SIZE - 7). Otherwise, data 
past (MAX_PAYLOAD_SIZE -7) will not be read**
  - Sevrer: Silent. After all the data promised in `size` has been delivered, return `COM` and resume normal operation.
If received an `EOF`, return `ACK` and resume normal operation
- Step 4: Close connection
  - Client: Send `END`. Wait until `ACK` then close the socket on client side
  - Server: Send `ACK` then read the stream until it receive `EndOfStream (-1 in Java)` 
(Indicate that client side has been closed) then close the socket on server side

### Stream reading behavior
- Both client and server will first read 7 bytes from stream. Then, depends on `[LENGTH]`, server will read `min([LENGTH], MAX_PAYLOAD_SIZE - 7)`

**Note: Read operation will become out-of-sync if `[LENGTH]` exceed `(MAX_PAYLOAD_SIZE -7)` or if actual data length is 
larger than `[LENGTH]` (Will happen if data is inputted from netcat because of \n)** 

## TODO
<details>
  <summary>Done</summary>

Java:
- ~~Backend~~
- ~~UI for send view~~
- ~~Implement UI for receiving~~

Android:
- ~~Adapt Java to Android~~
- ~~Implement devices discovery~~

</details>

Java:
- Implement devices discovery
- Implement UI for devices discovery 

Android:
- Make sure of UI-Logic seperation (Consider implement MVVM)

Common:
- Make UI better
- Move to TLS/SSL


## Issue Tracker

## License
GPLv3


