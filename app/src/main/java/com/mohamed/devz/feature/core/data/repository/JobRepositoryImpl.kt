package com.mohamed.devz.feature.core.data.repository

import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.domain.model.JobApplication
import com.mohamed.devz.feature.core.domain.model.JobPosting
import com.mohamed.devz.feature.core.domain.repository.JobRepository
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import java.io.IOException
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val remoteDataSource: DevZRemoteDataSource,
) : JobRepository {

    override suspend fun getApprovedJobPostings(): Result<List<JobPosting>, Error> {
        return try {
            val data = remoteDataSource.jobPosting.getApprovedJobPostings()
            Result.Success(data.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun getJobPostingById(id: Int): Result<JobPosting, Error> {
        return try {
            val data = remoteDataSource.jobPosting.getJobPostingById(id)
            Result.Success(data.toDomain())
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun getAllJobPostings(): Result<List<JobPosting>, Error> {
        return try {
            val data = remoteDataSource.jobPosting.getAllJobPostings()
            Result.Success(data.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun getJobPostingsByAccountId(accountId: Int): Result<List<JobPosting>, Error> {
        return try {
            val data = remoteDataSource.jobPosting.getJobPostingsByAccountId(accountId)
            Result.Success(data.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun insertJobPosting(posting: JobPosting): Result<JobPosting, Error> {
        return try {
            val data = remoteDataSource.jobPosting.insertJobPosting(posting.toData())
            Result.Success(data.toDomain())
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown(e.message ?: "Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun updateJobPosting(posting: JobPosting): Result<Unit, Error> {
        return try {
            remoteDataSource.jobPosting.updateJobPosting(posting.toData())
            Result.Success(Unit)
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun insertApplication(application: JobApplication): Result<JobApplication, Error> {
        return try {
            val data = remoteDataSource.jobApplication.insertJobApplication(application.toData())
            Result.Success(data.toDomain())
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun getApplicationsByApplicantId(applicantId: Int): Result<List<JobApplication>, Error> {
        return try {
            val data = remoteDataSource.jobApplication.getJobApplicationsByApplicantId(applicantId)
            Result.Success(data.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun getApplicationsByJobId(jobId: Int): Result<List<JobApplication>, Error> {
        return try {
            val data = remoteDataSource.jobApplication.getJobApplicationsByJobId(jobId)
            Result.Success(data.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun updateApplicationStatus(applicationId: Int, status: String): Result<Unit, Error> {
        return try {
            remoteDataSource.jobApplication.updateJobApplicationStatus(applicationId, status)
            Result.Success(Unit)
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }
}
