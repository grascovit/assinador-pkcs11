# Assinador

Assine documentos usando seu eToken SafeNet.

Requisitos:
- Java SE 1.8.0_261-b12 ou superior
- SafeNet Authentication Client (disponível no site da sua certificadora)

1. Crie um arquivo em qualquer diretório que contém o caminho para a biblioteca do driver do token/cartão:

    Por exemplo, no macOS:
    ```text
    name=OpenSC
    library=/usr/local/lib/libeTPkcs11.dylib
    ```

    O caminho varia de acordo com o sistema operacional:
    ```text
    Windows: C:\Windows\system32\etpkcs11.dll
    macOS: /usr/local/lib/libeTPkcs11.dylib
    Linux : /usr/lib/libeTPkcs11.so
    ```

2. Faça download do JAR que se encontra [aqui](https://github.com/grascovit/assinador-pkcs11/releases/latest/) e execute-o com o comando:
`java -jar caminho_para_o_.jar caminho_para_arquivo_config senha_do_token_ou_cartao`
3. Selecione um ou mais arquivos que deseja assinar
4. Selecione a pasta onde ficarão os arquivos assinados