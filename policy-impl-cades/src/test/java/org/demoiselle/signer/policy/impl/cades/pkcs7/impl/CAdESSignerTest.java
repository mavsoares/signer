/*
 * Demoiselle Framework
 * Copyright (C) 2016 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 *
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 *
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 *
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package org.demoiselle.signer.policy.impl.cades.pkcs7.impl;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStore.Builder;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;
import javax.net.ssl.KeyManagerFactory;
import org.demoiselle.signer.core.ca.manager.CAManagerConfiguration;
import org.demoiselle.signer.core.extension.BasicCertificate;
import org.demoiselle.signer.core.keystore.loader.KeyStoreLoader;
import org.demoiselle.signer.core.keystore.loader.factory.KeyStoreLoaderFactory;
import org.demoiselle.signer.core.keystore.loader.implementation.MSKeyStoreLoader;
import org.demoiselle.signer.cryptography.DigestAlgorithmEnum;
import org.demoiselle.signer.policy.engine.factory.PolicyFactory;
import org.demoiselle.signer.policy.impl.cades.SignatureInformations;
import org.demoiselle.signer.policy.impl.cades.SignerAlgorithmEnum;
import org.demoiselle.signer.policy.impl.cades.factory.PKCS7Factory;
import org.demoiselle.signer.policy.impl.cades.pkcs7.PKCS7Signer;
import org.junit.Test;


/**
 *
 */
@SuppressWarnings("unused")
public class CAdESSignerTest {

	// A anotação @Test está comentada, para passar o buld, pois as
	// configurações dependem de parâmetros
	// locais.

	/**
	 * 
	 * Faz a leitura do token, precisa setar a lib (.SO) e a senha do token.
	 */
	@SuppressWarnings("restriction")
	private KeyStore getKeyStoreToken() {

		try {
			// ATENÇÃO ALTERAR CONFIGURAÇÃO ABAIXO CONFORME O TOKEN USADO

			// Para TOKEN Branco a linha abaixo
			// String pkcs11LibraryPath =
			// "/usr/lib/watchdata/ICP/lib/libwdpkcs_icp.so";

			// Para TOKEN Azul a linha abaixo
			String pkcs11LibraryPath = "/usr/lib/libeToken.so";

			StringBuilder buf = new StringBuilder();
			buf.append("library = ").append(pkcs11LibraryPath).append("\nname = Provedor\n");
			Provider p = new sun.security.pkcs11.SunPKCS11(new ByteArrayInputStream(buf.toString().getBytes()));
			Security.addProvider(p);
			// ATENÇÃO ALTERAR "SENHA" ABAIXO
			Builder builder = KeyStore.Builder.newInstance("PKCS11", p,	new KeyStore.PasswordProtection("senha".toCharArray()));
			KeyStore ks;
			ks = builder.getKeyStore();

			return ks;

		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} finally {
		}

	}
	
	
	private KeyStore getKeyStoreTokenBySigner() {

		try {
			
			KeyStoreLoader keyStoreLoader = KeyStoreLoaderFactory.factoryKeyStoreLoader();
			KeyStore keyStore = keyStoreLoader.getKeyStore();

			return keyStore;

		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} finally {
		}

	}
	
	
	

	/**
	 * 
	 * Faz a leitura do certificado armazenado em arquivo (A1)
	 */

	private KeyStore getKeyStoreFile() {

		try {
			KeyStore ks = KeyStore.getInstance("pkcs12");

			// Alterar a senha
			char[] senha = "senha".toCharArray();

			// informar onde esta o arquivo
			InputStream ksIs = new FileInputStream("/home/{usuario}/certificado.p12");
			ks.load(ksIs, senha);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, senha);

			return ks;

		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}

	}
	
	
	/**
	 * 
	 * Keytore a partir de MSCAPI
	 */

	private KeyStore getKeyStoreOnWindows() {

		try {
			
			MSKeyStoreLoader msKeyStoreLoader = new MSKeyStoreLoader();
			
			KeyStore ks = msKeyStoreLoader.getKeyStore();

			return ks;

		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}

	}
	
	
	
	/**
	 * Teste com envio do conteúdo
	 */
	//@Test
	public void testSignDetached() {
		try {

			System.out.println("******** TESTANDO COM CONTEÚDO *****************");

			// INFORMAR o arquivo
			
			//
			 String fileDirName = "C:\\Users\\{usuario}\\arquivo_assinar.txt";
			
		
		
			

			byte[] fileToSign = readContent(fileDirName);

			// quando certificado em arquivo, precisa informar a senha
			char[] senha = "senha".toCharArray();

			// Para certificado em Token
			KeyStore ks = getKeyStoreTokenBySigner();

			// Para certificado em arquivo A1
			//KeyStore ks = getKeyStoreFile();

			// Para certificados no so windows (mascapi)
			// KeyStore ks = getKeyStoreOnWindows();
			
			String alias = getAlias(ks);
			/* Parametrizando o objeto doSign */
			PKCS7Signer signer = PKCS7Factory.getInstance().factoryDefault();
			signer.setCertificates(ks.getCertificateChain(alias));

			// para token
			signer.setPrivateKey((PrivateKey) ks.getKey(alias, null));

			// para arquivo
			//signer.setPrivateKey((PrivateKey) ks.getKey(alias, senha));
			// politica sem carimbo de tempo
			signer.setSignaturePolicy(PolicyFactory.Policies.AD_RB_CADES_2_2);
			// com carimbo de tempo
			// signer.setSignaturePolicy(PolicyFactory.Policies.AD_RT_CADES_2_2);

			// para mudar o algoritimo
			// signer.setAlgorithm(SignerAlgorithmEnum.SHA512withRSA);

			/* Realiza a assinatura do conteudo */
			System.out.println("Efetuando a  assinatura do conteudo");
			// Assinatura desatachada
			
			CAManagerConfiguration config = CAManagerConfiguration.getInstance();
			config.setCached(true);
			
			byte[] signature = signer.doDetachedSign(fileToSign);

			/* Valida o conteudo antes de gravar em arquivo */
			System.out.println("Efetuando a validacao da assinatura.");
			List<SignatureInformations> signaturesInfo = signer.checkDetattachedSignature(fileToSign, signature);

			if (signaturesInfo != null) {
				System.out.println("A assinatura foi validada.");
				assertTrue(true);
			} else {
				System.out.println("A assinatura foi invalidada!");
				assertTrue(false);
			}
			File file = new File(fileDirName + ".p7s");
			FileOutputStream os = new FileOutputStream(file);
			os.write(signature);
			os.flush();
			os.close();
			

		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | IOException ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	/**
	 * teste passando apenas o hash do arquivo
	 */
	//@Test
	public void testSignWithHash() {
		try {

			System.out.println("******** TESTANDO COM HASH *****************");

			// INFORMAR o arquivo para gerar o hash
			String fileDirName = "/home/{usuario}/arquivo_assinar.txt";
			
			
			byte[] fileToSign = readContent(fileDirName);

			// Para certificado em arquivo A1 é preciso essa senha para PrivateKey
			// para token troque a senha em: getKeyStoreToken()
			char[] senha = "senha".toCharArray();

			// gera o hash do arquivo
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance(DigestAlgorithmEnum.SHA_256.getAlgorithm());
			byte[] hash = md.digest(fileToSign);

			// Para certificado em arquivo A1
			// KeyStore ks = getKeyStoreFile();

			// Para certificados no so windows (mascapi)
			// KeyStore ks = getKeyStoreOnWindows();

			
			// Para certificado em token
			KeyStore ks = getKeyStoreToken();

			String alias = getAlias(ks);
			/* Parametrizando o objeto doSign */
			PKCS7Signer signer = PKCS7Factory.getInstance().factoryDefault();
			signer.setCertificates(ks.getCertificateChain(alias));

			// Para certificado em arquivo A1
			// signer.setPrivateKey((PrivateKey) ks.getKey(alias,senha));

			// Para certificado em token
			signer.setPrivateKey((PrivateKey) ks.getKey(alias, null));

			// Sem carimbo de tempo
			signer.setSignaturePolicy(PolicyFactory.Policies.AD_RB_CADES_2_2);

			// com carimbo de tempo
			// signer.setSignaturePolicy(PolicyFactory.Policies.AD_RT_CADES_2_2);

			// muda algoritmo
			// signer.setAlgorithm(SignerAlgorithmEnum.SHA256withRSA);

			/* Realiza a assinatura do conteudo */
			System.out.println("Efetuando a  assinatura do hash");
			byte[] signature = signer.doHashSign(hash);

			/* Valida o conteudo antes de gravar em arquivo */
			System.out.println("Efetuando a validacao da assinatura.");
			
			List<SignatureInformations> signaturesInfo = signer.checkDetattachedSignature(fileToSign, signature);

			if (signaturesInfo != null) {
				System.out.println("A assinatura foi validada.");
				assertTrue(true);
			} else {
				System.out.println("A assinatura foi invalidada!");
				assertTrue(false);
			}

			File file = new File(fileDirName + ".p7s");
			FileOutputStream os = new FileOutputStream(file);
			os.write(signature);
			os.flush();
			os.close();
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | IOException ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	/**
	 * Teste com envio do conteúdo
	 */
	//@Test
	public void testSignAttached() {
		try {
			
			System.out.println("******** TESTANDO COM CONTEÚDO *****************");

			// INFORMAR o arquivo
			String fileDirName = "/home/{usuario}/arquivo_assinar.txt";
			
			
			
			
			byte[] fileToSign = readContent(fileDirName);

			// quando certificado em arquivo, precisa informar a senha
			//char[] senha = "senha".toCharArray();

			// Para certificado em Token
			KeyStore ks = getKeyStoreToken();

			// Para certificado em arquivo A1
			// KeyStore ks = getKeyStoreFile();

			// Para certificados no so windows (mascapi)
			// KeyStore ks = getKeyStoreOnWindows();
			
			String alias = getAlias(ks);
			/* Parametrizando o objeto doSign */
			PKCS7Signer signer = PKCS7Factory.getInstance().factoryDefault();
			signer.setCertificates(ks.getCertificateChain(alias));

			// para token
			signer.setPrivateKey((PrivateKey) ks.getKey(alias, null));

			// para arquivo
			// signer.setPrivateKey((PrivateKey) ks.getKey(alias, senha));
			// politica sem carimbo de tempo
			signer.setSignaturePolicy(PolicyFactory.Policies.AD_RB_CADES_2_2);
			// com carimbo de tempo
			// signer.setSignaturePolicy(PolicyFactory.Policies.AD_RT_CADES_2_2);

			// para mudar o algoritimo
			// signer.setAlgorithm(SignerAlgorithmEnum.SHA512withRSA);

			/* Realiza a assinatura do conteudo */
			System.out.println("Efetuando a  assinatura do conteudo");
			// Com conteudo atachado
			byte[] signature = signer.doAttachedSign(fileToSign);

			boolean checked = false;
			/* Valida o conteudo antes de gravar em arquivo */
			System.out.println("Efetuando a validacao da assinatura.");
			List<SignatureInformations> signaturesInfo = signer.checkAttachedSignature(signature);

			if (signaturesInfo != null) {
				System.out.println("A assinatura foi validada.");
				assertTrue(true);
			} else {
				System.out.println("A assinatura foi invalidada!");
				assertTrue(false);
			}
			File file = new File(fileDirName + ".p7s");
			FileOutputStream os = new FileOutputStream(file);
			os.write(signature);
			os.flush();
			os.close();
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | IOException ex) {
			ex.printStackTrace();
			assertTrue(false);			
		}
	}

	/**
	 * Teste de coassinatura desanexada com envio do conteúdo
	 */
	//@Test
	public void testSignCoDetached() {
		try {

			System.out.println("******** TESTANDO COM CONTEÚDO *****************");

			// INFORMAR o arquivo
			String fileDirName = "local_e_nome_do_arquivo_para_assinar";
			String fileSignatureDirName = "local_e_nome_do_arquivo_da_assinatura";
			
			byte[] fileToSign = readContent(fileDirName);
			byte[] signatureFile = readContent(fileSignatureDirName);

			// quando certificado em arquivo, precisa informar a senha
			char[] senha = "senha".toCharArray();

			// Para certificado em Token
			KeyStore ks = getKeyStoreTokenBySigner();
			

			// Para certificado em arquivo A1
			// KeyStore ks = getKeyStoreFile();
			
			
			// Para certificados no so windows (mascapi)
			// KeyStore ks = getKeyStoreOnWindows();

			String alias = getAlias(ks);
			
			/* Parametrizando o objeto doSign */
			PKCS7Signer signer = PKCS7Factory.getInstance().factoryDefault();
			signer.setCertificates(ks.getCertificateChain(alias));

			// para token
			signer.setPrivateKey((PrivateKey) ks.getKey(alias, null));

			// para arquivo
			// signer.setPrivateKey((PrivateKey) ks.getKey(alias, senha));
			// politica sem carimbo de tempo
			signer.setSignaturePolicy(PolicyFactory.Policies.AD_RB_CADES_2_2);
			// com carimbo de tempo
			//signer.setSignaturePolicy(PolicyFactory.Policies.AD_RT_CADES_2_2);

			// para mudar o algoritimo
			// signer.setAlgorithm(SignerAlgorithmEnum.SHA512withRSA);

			/* Realiza a assinatura do conteudo */
			System.out.println("Efetuando a  assinatura do conteudo");
			// Assinatura desatachada
			byte[] signature = signer.doDetachedSign(fileToSign, signatureFile);

			/* Valida o conteudo antes de gravar em arquivo */
			System.out.println("Efetuando a validacao da assinatura.");
			List<SignatureInformations> signaturesInfo = signer.checkDetattachedSignature(fileToSign, signature);

			if (signaturesInfo != null) {
				System.out.println("A assinatura foi validada.");
				assertTrue(true);
			} else {
				System.out.println("A assinatura foi invalidada!");
				assertTrue(false);
			}
			File file = new File(fileDirName + "-co.p7s");
			FileOutputStream os = new FileOutputStream(file);
			os.write(signature);
			os.flush();
			os.close();
			

		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | IOException ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	
	/**
	 * Teste de coassinatura com envio do hash calculado
	 */
	//@Test
	public void testCoSignHash() {
		try {

			System.out.println("******** TESTANDO COM CONTEÚDO *****************");

			// INFORMAR o arquivo
			String fileDirName = "local_e_nome_do_arquivo_para_assinar";
			String fileSignatureDirName = "local_e_nome_do_arquivo_da_assinatura";
			

			byte[] fileToSign = readContent(fileDirName);
			byte[] signatureFile = readContent(fileSignatureDirName);
			
			
			// gera o hash do arquivo
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance(DigestAlgorithmEnum.SHA_256.getAlgorithm());
			byte[] hash = md.digest(fileToSign);

			// quando certificado em arquivo, precisa informar a senha
			char[] senha = "senha".toCharArray();

			// Para certificado em Token
			KeyStore ks = getKeyStoreToken();

			// Para certificado em arquivo A1
			// KeyStore ks = getKeyStoreFile();
			
			
			// Para certificados no so windows (mascapi)
			// KeyStore ks = getKeyStoreOnWindows();

			String alias = getAlias(ks);
			
			/* Parametrizando o objeto doSign */
			PKCS7Signer signer = PKCS7Factory.getInstance().factoryDefault();
			signer.setCertificates(ks.getCertificateChain(alias));

			// para token
			signer.setPrivateKey((PrivateKey) ks.getKey(alias, null));

			// para arquivo
			// signer.setPrivateKey((PrivateKey) ks.getKey(alias, senha));
			// politica sem carimbo de tempo
			signer.setSignaturePolicy(PolicyFactory.Policies.AD_RB_CADES_2_2);
			// com carimbo de tempo
			//signer.setSignaturePolicy(PolicyFactory.Policies.AD_RT_CADES_2_2);

			// para mudar o algoritimo
			// signer.setAlgorithm(SignerAlgorithmEnum.SHA512withRSA);

			/* Realiza a assinatura do conteudo */
			System.out.println("Efetuando a  assinatura do conteudo");
			// Assinatura desatachada
			byte[] signature = signer.doHashCoSign(hash, signatureFile);

			/* Valida o conteudo antes de gravar em arquivo */
			System.out.println("Efetuando a validacao da assinatura.");
			List<SignatureInformations> signaturesInfo = signer.checkDetattachedSignature(fileToSign, signature);

			if (signaturesInfo != null) {
				System.out.println("A assinatura foi validada.");
				assertTrue(true);
			} else {
				System.out.println("A assinatura foi invalidada!");
				assertTrue(false);
			}
			File file = new File(fileDirName + "-co.p7s");
			FileOutputStream os = new FileOutputStream(file);
			os.write(signature);
			os.flush();
			os.close();
			

		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | IOException ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}
	
	
//	@Test
	public void testVerifyDetachedSignature() {
		String fileToVerifyDirName = "local_e_nome_do_arquivo_assinado";
		String fileSignatureDirName = "local_e_nome_do_arquivo_da_assinatura";
		
		
		
		
		byte[] fileToVerify = readContent(fileToVerifyDirName);
		byte[] signatureFile = readContent(fileSignatureDirName);
		
		PKCS7Signer signer = PKCS7Factory.getInstance().factoryDefault();

		System.out.println("Efetuando a validacao da assinatura");
		List<SignatureInformations> signaturesInfo = signer.checkDetattachedSignature(fileToVerify, signatureFile);
		
		if (signaturesInfo != null) {
			System.out.println("A assinatura foi validada.");
			for (SignatureInformations si : signaturesInfo){
				System.out.println(si.getSignDate());
				if (si.getTimeStampSigner() != null){
					System.out.println("Serial"+si.getTimeStampSigner().toString());
				}
				for(X509Certificate cert : si.getChain()){
					BasicCertificate certificate = new BasicCertificate(cert);
					if (!certificate.isCACertificate()){
						System.out.println(certificate.toString());
					}												
				}
				for (String valErr : si.getValidatorErrors()){
					System.out.println(valErr);
				}				
			}
			assertTrue(true);	
		
		} else {
			System.out.println("A assinatura foi invalidada!");
			assertTrue(false);
		}
	}

	
	//@Test
	public void testVerifyAttachedSignature() {
		String fileSignatureDirName = "local_e_nome_do_arquivo_da_assinatura";
		byte[] signatureFile = readContent(fileSignatureDirName);

		PKCS7Signer signer = PKCS7Factory.getInstance().factoryDefault();

		System.out.println("Efetuando a validacao da assinatura");
		List<SignatureInformations> signaturesInfo =  signer.checkAttachedSignature(signatureFile);
		if (signaturesInfo != null) {
			System.out.println("A assinatura foi validada.");
			for (SignatureInformations si : signaturesInfo){
				System.out.println(si.getSignDate());
				if (si.getTimeStampSigner() != null){
					System.out.println("Serial"+si.getTimeStampSigner().toString());
				}
				for(X509Certificate cert : si.getChain()){
					BasicCertificate certificate = new BasicCertificate(cert);
					if (!certificate.isCACertificate()){
						System.out.println(certificate.toString());
					}												
				}
				System.out.println(si.getSignaturePolicy().toString());
			}
			assertTrue(true);		
		
		} else {
			System.out.println("A assinatura foi invalidada!");
			assertTrue(false);
		}
	}
	
	
	// @Test
	public void testVerifySignatureByHash() {
		String fileSignatureDirName = "local_e_nome_do_arquivo_da_assinatura";
		String fileToVerifyDirName = "local_e_nome_do_arquivo_assinado";
		
		
							
		byte[] fileToVerify = readContent(fileToVerifyDirName);
				
		byte[] signatureFile = readContent(fileSignatureDirName);

		java.security.MessageDigest md;
		try {
			md = java.security.MessageDigest
					.getInstance(DigestAlgorithmEnum.SHA_256.getAlgorithm());
		
			// gera o hash do arquivo que foi assinado
			byte[] hash = md.digest(fileToVerify);
		
			PKCS7Signer signer = PKCS7Factory.getInstance().factoryDefault();
		
			System.out.println("Efetuando a validacao da assinatura");
					
			List<SignatureInformations> signaturesInfo = signer.checkSignatureByHash(SignerAlgorithmEnum.SHA256withRSA.getOIDAlgorithmHash(), hash, signatureFile);
			if (signaturesInfo != null) {
				System.out.println("A assinatura foi validada.");
				for (SignatureInformations si : signaturesInfo){
					System.out.println(si.getSignDate());
					if (si.getTimeStampSigner() != null){
						System.out.println("Serial"+si.getTimeStampSigner().toString());
					}
					for(X509Certificate cert : si.getChain()){
						BasicCertificate certificate = new BasicCertificate(cert);
						if (!certificate.isCACertificate()){
							System.out.println(certificate.toString());
						}												
					}
					System.out.println(si.getSignaturePolicy().toString());
				}
				assertTrue(true);
			} else {
				System.out.println("A assinatura foi invalidada!");
				assertTrue(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	private byte[] readContent(String parmFile) {
		byte[] result = null;
		try {
			File file = new File(parmFile);
			FileInputStream is = new FileInputStream(parmFile);
			result = new byte[(int) file.length()];
			is.read(result);
			is.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	private String getAlias(KeyStore ks) {
		Certificate[] certificates = null;
		String alias = "";
		Enumeration<String> e;
		try {
			e = ks.aliases();
			while (e.hasMoreElements()) {
				alias = e.nextElement();
				System.out.println("alias..............: " + alias);
				System.out.println("iskeyEntry"+ ks.isKeyEntry(alias));
				System.out.println("containsAlias"+ks.containsAlias(alias));
				//System.out.println(""+ks.getKey(alias, null));
				certificates = ks.getCertificateChain(alias);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return alias;
	}
}