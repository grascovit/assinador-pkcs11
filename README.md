# Assinador

Assine documentos usando seu eToken.
Testado com tokens SafeNet 5110 e GD Burti StarSign.

Requisitos:
- Java SE 1.8.0_261-b12 ou superior
- SafeNet Authentication Client (disponível no site da sua certificadora)

1. Crie um arquivo em qualquer diretório que contém o caminho para o driver do token:

    Por exemplo, no macOS usando um token SafeNet 5110:
    ```text
    name=OpenSC
    library=/usr/local/lib/libeTPkcs11.dylib
    ```

    O caminho varia de acordo com o sistema operacional e a fabricante do token.

    Alguns exemplos para os tokens SafeNet:
    ```text
    Windows: C:\Windows\system32\etpkcs11.dll
    macOS: /usr/local/lib/libeTPkcs11.dylib
    Linux : /usr/lib/libeTPkcs11.so
    ```

   Alguns exemplos para os tokens GD Burti StarSign:

   ```text
   Windows: C:\WINDOWS\system32\aetpkss1.dll
   macOS: /usr/local/lib/libaetpkss.dylib
   Linux : /usr/lib/libaetpkss.so
   ```

2. Faça download do JAR que se encontra [aqui](https://github.com/grascovit/assinador-pkcs11/releases/latest/) e execute-o com o comando:
`java -jar caminho_para_o_.jar caminho_para_arquivo_config senha_do_token_ou_cartao`
3. Selecione um ou mais arquivos que deseja assinar
4. Selecione a pasta onde ficarão os arquivos assinados